package com.sprint.findex.service;

import com.sprint.findex.client.MarketIndexApiClient;
import com.sprint.findex.dto.openapi.MarketIndexApiRequest;
import com.sprint.findex.dto.openapi.MarketIndexApiResponse;
import com.sprint.findex.dto.openapi.MarketIndexApiResponse.Item;
import com.sprint.findex.dto.sync.IndexInfoLookup;
import com.sprint.findex.dto.sync.IndexInfoSyncSource;
import com.sprint.findex.dto.sync.SyncJobDto;
import com.sprint.findex.entity.IndexInfo;
import com.sprint.findex.enums.SourceType;
import com.sprint.findex.mapper.MarketIndexApiSyncMapper;
import com.sprint.findex.repository.IndexInfoRepository;
import com.sprint.findex.util.IndexNameResolver;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
    private final MarketIndexApiSyncMapper marketIndexApiSyncMapper;
    private final IndexNameResolver indexNameResolver;
    private final IndexInfoRepository indexInfoRepository;
    private final IndexInfoSyncService indexInfoSyncService;
    private final IndexInfoSyncFailureService indexInfoSyncFailureService;

    public List<SyncJobDto> syncIndexInfos(String worker) {
        // Open API 데이터가 존재하는 최신 기준일자 조회
        String latestAvailableBaseDate = findLatestAvailableBaseDate();
        // 해당 기준일자의 모든 지수 데이터 조회
        List<Item> allItems = fetchAllItems(latestAvailableBaseDate);

        // 기존에 존재하는 OPEN_API 타입의 지수 목록을 조회
        Map<LookupKey, IndexInfoLookup> lookupMap = buildOpenApiIndexLookupMap();
        List<SyncJobDto> syncJobs = new ArrayList<>();

        for (Item item : allItems) {
            // 작업 시각은 item별로 기록
            Instant jobTime = Instant.now();

            // Open API 응답 데이터를 IndexInfoSyncSource 객체로 변환
            IndexInfoSyncSource source = marketIndexApiSyncMapper.toSource(item);
            // 변경된 지수명에 해당할 수 있으므로 확인
            String standardName = indexNameResolver.resolveStandardName(
                    source.indexClassification(),
                    source.indexName()
            );

            LookupKey key = new LookupKey(source.indexClassification(), standardName);
            IndexInfoLookup lookup = lookupMap.get(key);

            try {
                // Item 단위로 독립 커밋을 진행하여 지수별 성공/실패를 따로 기록
                SyncJobDto successJob = indexInfoSyncService.syncSingleItem(
                        source,
                        standardName,
                        lookup,
                        worker,
                        jobTime
                );
                syncJobs.add(successJob);
            } catch (Exception e) {
                try {
                    // 연동 실패 시 실패 이력 저장 시도
                    SyncJobDto failureJob = indexInfoSyncFailureService.saveFailure(
                            lookup,
                            worker,
                            jobTime,
                            e
                    );
                    if (failureJob != null) {
                        syncJobs.add(failureJob);
                    }
                } catch (Exception ignored) {
                }
            }
        }

        return syncJobs;
    }

    private record LookupKey(String indexClassification, String standardName) {

    }

    private Map<LookupKey, IndexInfoLookup> buildOpenApiIndexLookupMap() {
        // 현재 DB에 저장된 OPEN_API 타입의 지수 정보를 한번에 조회
        List<IndexInfo> existingIndexInfos = indexInfoRepository.findAllBySourceType(
                SourceType.OPEN_API);

        // (지수 분류, 지수 이름) 기준으로 지수 정보 분류
        Map<LookupKey, IndexInfoLookup> lookupMap = new HashMap<>();

        for (IndexInfo indexInfo : existingIndexInfos) {
            // 변경된 지수명에 해당할 수 있으므로 확인
            String standardName = indexNameResolver.resolveStandardName(
                    indexInfo.getIndexClassification(),
                    indexInfo.getIndexName()
            );

            // lookupMap에서 사용할 key 생성
            // (지수 분류, 지수 이름)으로 구분
            LookupKey key = new LookupKey(indexInfo.getIndexClassification(), standardName);
            lookupMap.putIfAbsent(key, new IndexInfoLookup(indexInfo.getId()));
        }

        // OPEN_API 타입의 지수 정보(id) 목록이 담긴 map 반환
        return lookupMap;
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
}
