package com.sprint.findex.service;

import com.sprint.findex.client.MarketIndexApiClient;
import com.sprint.findex.dto.openapi.MarketIndexApiRequest;
import com.sprint.findex.dto.openapi.MarketIndexApiResponse;
import com.sprint.findex.dto.openapi.MarketIndexApiResponse.Item;
import com.sprint.findex.dto.sync.IndexInfoSyncSource;
import com.sprint.findex.dto.sync.SyncJobDto;
import com.sprint.findex.entity.IndexInfo;
import com.sprint.findex.entity.IntegrationTask;
import com.sprint.findex.enums.JobResult;
import com.sprint.findex.enums.JobType;
import com.sprint.findex.enums.SourceType;
import com.sprint.findex.mapper.MarketIndexApiSyncMapper;
import com.sprint.findex.mapper.SyncJobMapper;
import com.sprint.findex.repository.IndexInfoRepository;
import com.sprint.findex.repository.IntegrationTaskRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class IndexSyncService {

  // Open API의 기준일자 형식
  private static final DateTimeFormatter BASIC_DATE_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;
  // 한국 시간 기준
  private static final ZoneId KST = ZoneId.of("Asia/Seoul");
  // Open API 최대 조회 단위
  private static final int PAGE_SIZE = 100;
  // 최신 기준일자를 찾기 위해 최대 30일 전까지 조회
  private static final int BASE_DATE_LOOKBACK_DAYS = 30;

  private final MarketIndexApiClient marketIndexApiClient;
  private final IndexInfoRepository indexInfoRepository;
  private final IntegrationTaskRepository integrationTaskRepository;
  private final MarketIndexApiSyncMapper marketIndexApiSyncMapper;
  private final SyncJobMapper syncJobMapper;

  @Transactional
  public List<SyncJobDto> syncIndexInfos(String worker) {
    // Open API 데이터가 존재하는 최신 기준일자 조회
    String latestAvailableBaseDate = findLatestAvailableBaseDate();
    // 해당 기준일자의 모든 지수 데이터 조회
    List<Item> allItems = fetchAllItems(latestAvailableBaseDate);

    List<IntegrationTask> integrationTasks = new ArrayList<>();
    for (Item item : allItems) {
      try {
        // IndexInfo 업데이트 또는 생성
        IndexInfo indexInfo = updateOrCreateIndexInfo(item);

        // 연동 작업 이력 생성
        // INDEX_INFO 작업 전용, targetDate는 사용하지 않기 때문에 null 처리
        IntegrationTask integrationTask = IntegrationTask.create(
            indexInfo,
            JobType.INDEX_INFO.name(),
            null,
            worker,
            Instant.now(),
            JobResult.SUCCESS.name()
        );

        integrationTasks.add(integrationTask);
      } catch (Exception e) {
        // 연동 실패
        IntegrationTask failedTask = createFailedTask(item, worker, Instant.now(), e);
        if (failedTask != null) {
          integrationTasks.add(failedTask);
        }
      }
    }

    // 연동 작업 목록 저장
    List<IntegrationTask> savedIntegrationTasks = integrationTaskRepository.saveAll(
        integrationTasks);

    // 저장된 연동 작업 목록을 DTO로 변환하여 List로 반환
    return savedIntegrationTasks.stream()
        .map(syncJobMapper::toDto)
        .toList();
  }

  private IndexInfo updateOrCreateIndexInfo(Item item) {
    // Open API 응답 데이터를 IndexInfoSyncSource 객체로 변환
    IndexInfoSyncSource source = marketIndexApiSyncMapper.toSource(item);

    return indexInfoRepository.findByIndexClassificationAndIndexName(
            source.indexClassification(),
            source.indexName()
        )
        .map(existing -> {
          // Open API 데이터인 경우에만 업데이트
          if (existing.getSourceType() == SourceType.OPEN_API) {
            existing.updateIndexInfo(
                source.employedItemsCount(),
                source.basePointInTime(),
                source.baseIndex(),
                existing.getFavorite()
            );
          }
          return existing;
        })
        // 데이터베이스에 존재하지 않으면 새로운 IndexInfo 생성
        .orElseGet(() -> indexInfoRepository.save(
            IndexInfo.create(
                source.indexName(),
                source.indexClassification(),
                source.employedItemsCount(),
                source.basePointInTime(),
                source.baseIndex(),
                SourceType.OPEN_API,
                false
            )
        ));
  }

  private List<Item> fetchAllItems(String baseDate) {
    // 첫 페이지 조회
    MarketIndexApiResponse firstPageResponse = requestMarketIndex(baseDate, 1, PAGE_SIZE);
    List<Item> allItems = new ArrayList<>(firstPageResponse.response().body().itemList());

    // 전체 데이터 개수
    int totalCount = Integer.parseInt(firstPageResponse.response().body().totalCount());
    // 전체 페이지 수
    int totalPages = (totalCount + PAGE_SIZE - 1) / PAGE_SIZE;

    // 나머지 페이지를 조회하며 데이터 추가
    for (int pageNo = 2; pageNo <= totalPages; pageNo++) {
      MarketIndexApiResponse pageResponse = requestMarketIndex(baseDate, pageNo, PAGE_SIZE);
      allItems.addAll(pageResponse.response().body().itemList());
    }

    return allItems;
  }

  private String findLatestAvailableBaseDate() {
    LocalDate date = LocalDate.now(KST);
    LocalDate lowerBound = date.minusDays(BASE_DATE_LOOKBACK_DAYS);

    while (!date.isBefore(lowerBound)) {
      String baseDate = date.format(BASIC_DATE_FORMATTER);
      // 해당 날짜의 데이터 존재 여부 확인
      MarketIndexApiResponse response = requestMarketIndex(baseDate, 1, 1);
      if (!response.response().body().itemList().isEmpty()) {
        return baseDate;
      }

      // 데이터가 존재하지 않으면 하루 전으로 이동
      date = date.minusDays(1);
    }

    // 예외 개선 필요 - 데이터가 존재하는 기준 일자를 찾지 못함
    throw new RuntimeException();
  }

  private MarketIndexApiResponse requestMarketIndex(String baseDate, int pageNo, int numOfRows) {
    // Open API 요청 DTO 생성
    MarketIndexApiRequest request = MarketIndexApiRequest.builder()
        .basDt(baseDate)
        .pageNo(pageNo)
        .numOfRows(numOfRows)
        .build();

    // Open API 호출
    return marketIndexApiClient.getMarketIndex(request);
  }

  private IntegrationTask createFailedTask(Item item, String worker, Instant jobTime, Exception e) {
    IndexInfoSyncSource source = marketIndexApiSyncMapper.toSource(item);

    IndexInfo indexInfo = indexInfoRepository.findByIndexClassificationAndIndexName(
            source.indexClassification(),
            source.indexName()
        )
        .orElse(null);

    if (indexInfo == null) {
      return null;
    }

    return IntegrationTask.create(
        indexInfo,
        JobType.INDEX_INFO.name(),
        null,
        worker,
        jobTime,
        JobResult.FAILED.name(),
        // 실패 메시지 개선 필요
        e.getMessage()
    );
  }
}
