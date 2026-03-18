package com.sprint.findex.service;

import com.sprint.findex.client.MarketIndexApiClient;
import com.sprint.findex.dto.openapi.MarketIndexApiRequest;
import com.sprint.findex.dto.openapi.MarketIndexApiResponse;
import com.sprint.findex.dto.openapi.MarketIndexApiResponse.Item;
import com.sprint.findex.dto.sync.IndexDataSyncRequest;
import com.sprint.findex.dto.sync.IndexDataSyncSource;
import com.sprint.findex.dto.sync.IndexInfoLookup;
import com.sprint.findex.dto.sync.IndexInfoSyncSource;
import com.sprint.findex.dto.sync.SyncJobDto;
import com.sprint.findex.entity.IndexData;
import com.sprint.findex.entity.IndexInfo;
import com.sprint.findex.enums.SourceType;
import com.sprint.findex.exception.BusinessLogicException;
import com.sprint.findex.exception.ExceptionCode;
import com.sprint.findex.mapper.MarketIndexApiSyncMapper;
import com.sprint.findex.repository.IndexDataRepository;
import com.sprint.findex.repository.IndexInfoRepository;
import com.sprint.findex.util.IndexNameResolver;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class IndexSyncService {

    private static final DateTimeFormatter BASIC_DATE_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");
    private static final int PAGE_SIZE = 100;
    private static final int BASE_DATE_LOOKBACK_DAYS = 30;

    private final MarketIndexApiClient marketIndexApiClient;
    private final MarketIndexApiSyncMapper marketIndexApiSyncMapper;
    private final IndexNameResolver indexNameResolver;
    private final IndexInfoRepository indexInfoRepository;
    private final IndexDataRepository indexDataRepository;
    private final IndexInfoSyncService indexInfoSyncService;
    private final IndexInfoSyncFailureService indexInfoSyncFailureService;
    private final IndexDataSyncService indexDataSyncService;
    private final IndexDataSyncFailureService indexDataSyncFailureService;

    public List<SyncJobDto> syncIndexInfo(String worker) {
        // Open API 데이터가 존재하는 최신 기준일자 조회
        String latestAvailableBaseDate = findLatestAvailableBaseDate();
        // 해당 기준일자의 모든 지수 데이터 조회
        List<Item> allItems = fetchAllItemsByBaseDate(latestAvailableBaseDate);

        // 기존에 존재하는 OPEN_API 타입의 지수 목록을 조회
        Map<IndexInfoLookupKey, IndexInfoLookup> lookupMap = buildIndexInfoLookupMap();

        List<SyncJobDto> syncJobs = new ArrayList<>();
        for (Item item : allItems) {
            // 작업 시각은 item별로 기록
            Instant jobTime = Instant.now();

            // Open API 응답 데이터를 IndexInfoSyncSource 객체로 변환
            IndexInfoSyncSource source = marketIndexApiSyncMapper.toInfoSource(item);

            // 지수 분류와 표준 지수명을 기준으로 비교 키 생성
            IndexInfoLookupKey key = buildLookupKey(
                    source.indexClassification(),
                    source.indexName()
            );

            // 기존 DB에 같은 지수가 있는지 조회
            IndexInfoLookup lookup = lookupMap.get(key);

            try {
                // Item 단위로 독립 커밋을 진행하여 지수별 성공/실패를 따로 기록
                SyncJobDto successJob = indexInfoSyncService.syncSingleItem(
                        source,
                        key.standardName(),
                        lookup,
                        worker,
                        jobTime
                );
                syncJobs.add(successJob);
            } catch (Exception syncException) {
                try {
                    // 연동 실패 시 실패 이력 저장 시도
                    SyncJobDto failureJob = indexInfoSyncFailureService.saveFailure(
                            lookup,
                            worker,
                            jobTime,
                            syncException.getMessage()
                    );
                    // 실패 이력 저장에 성공하면 응답 목록에 포함
                    if (failureJob != null) {
                        syncJobs.add(failureJob);
                    }
                } catch (Exception failureSaveException) {
                    // 실패 이력조차 저장되지 않으면 서버 로그 생성
                    log.error("Failed to save sync failure history", failureSaveException);
                }
            }
        }

        return syncJobs;
    }

    // 자동 연동
    public void autoSyncIndexData(List<IndexDataSyncRequest> requests, String worker) {
        for (IndexDataSyncRequest request : requests) {
            // 개별 연동 실행
            syncIndexData(request, worker);
        }
    }

    public List<SyncJobDto> syncIndexData(IndexDataSyncRequest request, String worker) {
        List<UUID> indexInfoIds = request.indexInfoIds();
        LocalDate baseDateFrom = request.baseDateFrom();
        LocalDate baseDateTo = request.baseDateTo();

        List<IndexInfo> targetIndexInfos = resolveTargetIndexInfo(indexInfoIds);
        // 연동 대상 지수가 없으면 추가 처리 없이 종료
        if (targetIndexInfos.isEmpty()) {
            return List.of();
        }

        // 대상 지수 개수에 따라 직접 조회 또는 전체 조회 전략을 적용
        List<Item> allItems = fetchSyncItems(targetIndexInfos, baseDateFrom, baseDateTo);

        // Open API 조회 결과가 없으면 추가 처리 없이 종료
        if (allItems.isEmpty()) {
            return List.of();
        }

        // 조회된 지수를 빠르게 찾기 위해 표준화된 키 기반 map 생성
        // 저장 시 indexInfoId로 원본 IndexInfo를 바로 찾기 위한 map 생성
        Map<IndexInfoLookupKey, IndexInfo> targetIndexInfoMap = new HashMap<>();
        Map<UUID, IndexInfo> targetIndexInfoById = new HashMap<>();

        for (IndexInfo indexInfo : targetIndexInfos) {
            targetIndexInfoMap.put(
                    buildLookupKey(indexInfo.getIndexClassification(), indexInfo.getIndexName()),
                    indexInfo
            );
            targetIndexInfoById.put(indexInfo.getId(), indexInfo);
        }

        // Open API 응답 중 실제 연동 대상 지수에 해당하는 데이터만 추출
        Map<IndexDataLookupKey, Item> itemMap = buildItemMap(allItems, targetIndexInfoMap);

        // 매칭되는 데이터가 없으면 추가 처리 없이 종료
        if (itemMap.isEmpty()) {
            return List.of();
        }

        // 연동 대상 지수의 id 수집
        List<UUID> targetIndexInfoIds = targetIndexInfos.stream()
                .map(IndexInfo::getId)
                .toList();

        // 기존 지수 데이터를 미리 조회해 생성/수정 판단에 사용
        Map<IndexDataLookupKey, IndexData> indexDataLookupMap =
                buildIndexDataLookupMap(targetIndexInfoIds, baseDateFrom, baseDateTo);

        List<SyncJobDto> syncJobs = new ArrayList<>();
        for (Map.Entry<IndexDataLookupKey, Item> entry : itemMap.entrySet()) {
            IndexDataLookupKey key = entry.getKey();
            IndexInfo indexInfo = targetIndexInfoById.get(key.indexInfoId());
            Item item = entry.getValue();

            // 작업마다 시간 생성
            Instant jobTime = Instant.now();

            try {
                IndexDataSyncSource source = marketIndexApiSyncMapper.toDataSource(item);
                IndexData existingIndexData = indexDataLookupMap.get(key);

                SyncJobDto successJob = indexDataSyncService.syncSingleItem(
                        indexInfo,
                        key.baseDate(),
                        source,
                        existingIndexData,
                        worker,
                        jobTime
                );
                syncJobs.add(successJob);
            } catch (Exception syncException) {
                try {
                    SyncJobDto failureJob = indexDataSyncFailureService.saveFailure(
                            indexInfo,
                            key.baseDate(),
                            worker,
                            jobTime,
                            syncException.getMessage()
                    );
                    // 실패 이력 저장에 성공하면 응답 목록에 포함
                    if (failureJob != null) {
                        syncJobs.add(failureJob);
                    }
                } catch (Exception failureSaveException) {
                    log.error("Failed to save sync failure history", failureSaveException);
                }
            }
        }

        return syncJobs;
    }

    private record IndexInfoLookupKey(String indexClassification, String standardName) {

    }

    private record IndexDataLookupKey(UUID indexInfoId, LocalDate baseDate) {

    }

    private record ApiItemKey(String indexClassification, String indexName, String baseDate) {

    }

    private Map<IndexInfoLookupKey, IndexInfoLookup> buildIndexInfoLookupMap() {
        // 현재 DB에 저장된 OPEN_API 타입의 지수 정보를 한번에 조회
        List<IndexInfo> existingIndexInfoList = indexInfoRepository.findAllBySourceType(
                SourceType.OPEN_API);

        // (지수 분류, 지수 이름) 기준으로 지수 정보 분류
        Map<IndexInfoLookupKey, IndexInfoLookup> lookupMap = new HashMap<>();
        for (IndexInfo indexInfo : existingIndexInfoList) {
            // lookupMap에서 사용할 key 생성
            // (지수 분류, 지수 이름)으로 구분
            IndexInfoLookupKey key = buildLookupKey(
                    indexInfo.getIndexClassification(),
                    indexInfo.getIndexName()
            );
            lookupMap.putIfAbsent(key, new IndexInfoLookup(indexInfo.getId()));
        }

        // OPEN_API 타입의 지수 정보(id) 목록이 담긴 map 반환
        return lookupMap;
    }

    private Map<IndexDataLookupKey, IndexData> buildIndexDataLookupMap(
            List<UUID> indexInfoIds,
            LocalDate baseDateFrom,
            LocalDate baseDateTo
    ) {
        // 대상 지수 id가 없으면 기존 데이터 조회 없이 빈 map 반환
        if (indexInfoIds == null || indexInfoIds.isEmpty()) {
            return Map.of();
        }

        // 지정한 기간 내 기존 지수 데이터를 한번에 조회
        List<IndexData> existingIndexDataList =
                indexDataRepository.findAllByIndexInfoIdInAndBaseDateBetween(
                        indexInfoIds,
                        baseDateFrom,
                        baseDateTo
                );

        Map<IndexDataLookupKey, IndexData> lookupMap = new HashMap<>();
        for (IndexData indexData : existingIndexDataList) {
            lookupMap.put(
                    new IndexDataLookupKey(
                            indexData.getIndexInfo().getId(),
                            indexData.getBaseDate()
                    ),
                    indexData
            );
        }

        return lookupMap;
    }

    private Map<IndexDataLookupKey, Item> buildItemMap(
            List<Item> items,
            Map<IndexInfoLookupKey, IndexInfo> targetIndexInfoMap
    ) {
        // Open API 응답을 (지수 id, 날짜) 기준의 연동 대상 map으로 변환
        Map<IndexDataLookupKey, Item> itemMap = new HashMap<>();

        for (Item item : items) {
            LocalDate baseDate = LocalDate.parse(item.basDt(), BASIC_DATE_FORMATTER);
            IndexInfoLookupKey key = buildLookupKey(item.idxCsf(), item.idxNm());
            IndexInfo indexInfo = targetIndexInfoMap.get(key);

            // 현재 요청 대상 지수와 매칭되지 않으면 제외
            if (indexInfo == null) {
                continue;
            }

            // 같은 지수와 날짜의 데이터는 최초 1건만 사용
            itemMap.putIfAbsent(new IndexDataLookupKey(indexInfo.getId(), baseDate), item);
        }

        return itemMap;
    }

    private IndexInfoLookupKey buildLookupKey(String indexClassification, String indexName) {
        String normalizedIndexClassification = indexNameResolver.normalizeIndexClassification(
                indexClassification
        );

        // 변경된 지수명에 해당할 수 있으므로 확인
        String standardName = indexNameResolver.resolveStandardName(
                normalizedIndexClassification,
                indexName
        );

        return new IndexInfoLookupKey(normalizedIndexClassification, standardName);
    }

    private List<Item> fetchSyncItems(
            List<IndexInfo> targetIndexInfos,
            LocalDate baseDateFrom,
            LocalDate baseDateTo
    ) {
        // 다중 지수 요청이면 idxNm 직접 조회 대신 전체 조회 전략 사용
        if (!shouldUseTargetedIndexNameFetch(targetIndexInfos)) {
            return fetchAllItemsByDateRange(baseDateFrom, baseDateTo);
        }

        List<String> searchNames = buildTargetIndexSearchNames(targetIndexInfos);

        // 검색 가능한 이름이 없으면 전체 조회 전략 사용
        if (searchNames.isEmpty()) {
            return fetchAllItemsByDateRange(baseDateFrom, baseDateTo);
        }

        // 현재 수동 연동과 자동 연동은 모두 단일 지수 요청 흐름이므로
        // 대상 지수가 1개일 때만 idxNm 직접 조회 전략을 사용
        List<Item> targetedItems = fetchAllItemsByDateRange(
                baseDateFrom,
                baseDateTo,
                searchNames
        );

        // idxNm 직접 조회는 Open API의 이름 매칭 결과에 영향을 받을 수 있으므로
        // 결과가 없으면 전체 조회로 다시 시도
        if (!targetedItems.isEmpty()) {
            return targetedItems;
        }

        return fetchAllItemsByDateRange(baseDateFrom, baseDateTo);
    }

    private boolean shouldUseTargetedIndexNameFetch(List<IndexInfo> targetIndexInfos) {
        // 현재 실제 요청 구조상 수동 연동은 1개 지수 선택,
        // 자동 연동도 1개 지수씩 순차 요청이므로 단일 지수일 때만 직접 조회
        return targetIndexInfos.size() == 1;
    }

    private List<String> buildTargetIndexSearchNames(List<IndexInfo> targetIndexInfos) {
        LinkedHashSet<String> searchNames = new LinkedHashSet<>();

        for (IndexInfo indexInfo : targetIndexInfos) {
            // 지수명 변경 이력이나 별칭까지 포함한 검색 이름 목록 구성
            searchNames.addAll(indexNameResolver.buildSearchNames(
                    indexInfo.getIndexClassification(),
                    indexInfo.getIndexName()
            ));
        }

        return List.copyOf(searchNames);
    }

    private List<IndexInfo> resolveTargetIndexInfo(List<UUID> indexInfoIds) {
        if (indexInfoIds == null || indexInfoIds.isEmpty()) {
            // 스펙상 지수 정보 ID 목록이 비어있을 경우 모든 지수 대상
            // IndexDataSyncRequest 참고
            return indexInfoRepository.findAll();
        }

        // 중복 id를 제거한 뒤 실제 DB 조회 수행
        LinkedHashSet<UUID> requestedIds = new LinkedHashSet<>(indexInfoIds);
        List<IndexInfo> foundIndexInfos = indexInfoRepository.findAllById(requestedIds);

        // 요청 개수와 조회 개수가 같은지 확인
        if (foundIndexInfos.size() != requestedIds.size()) {
            throw new BusinessLogicException(ExceptionCode.INDEX_INFO_NOT_FOUND);
        }

        return foundIndexInfos;
    }

    private List<Item> fetchAllItemsByBaseDate(String baseDate) {
        MarketIndexApiResponse firstPageResponse = requestMarketIndex(baseDate, 1, PAGE_SIZE);
        List<Item> allItems = new ArrayList<>(extractItems(firstPageResponse));

        int totalCount = extractTotalCount(firstPageResponse);
        int totalPages = calculateTotalPages(totalCount);

        // 나머지 페이지를 조회하며 데이터 추가
        for (int pageNo = 2; pageNo <= totalPages; pageNo++) {
            MarketIndexApiResponse pageResponse = requestMarketIndex(baseDate, pageNo, PAGE_SIZE);
            allItems.addAll(extractItems(pageResponse));
        }

        return allItems;
    }

    private List<Item> fetchAllItemsByDateRange(LocalDate baseDateFrom, LocalDate baseDateTo) {
        return fetchAllItemsByDateRange(baseDateFrom, baseDateTo, (String) null);
    }

    private List<Item> fetchAllItemsByDateRange(
            LocalDate baseDateFrom,
            LocalDate baseDateTo,
            String indexName
    ) {
        String beginDate = formatBaseDate(baseDateFrom);

        // 종료일 포함 조회를 위해 다음 날을 exclusive end로 사용
        String endDateExclusive = formatBaseDate(baseDateTo.plusDays(1));

        MarketIndexApiResponse firstPageResponse = requestMarketIndex(
                beginDate,
                endDateExclusive,
                indexName,
                1
        );
        List<Item> allItems = new ArrayList<>(extractItems(firstPageResponse));

        int totalCount = extractTotalCount(firstPageResponse);
        int totalPages = calculateTotalPages(totalCount);

        // 나머지 페이지를 조회하며 데이터 추가
        for (int pageNo = 2; pageNo <= totalPages; pageNo++) {
            MarketIndexApiResponse pageResponse = requestMarketIndex(
                    beginDate,
                    endDateExclusive,
                    indexName,
                    pageNo
            );
            allItems.addAll(extractItems(pageResponse));
        }

        return allItems;
    }

    private List<Item> fetchAllItemsByDateRange(
            LocalDate baseDateFrom,
            LocalDate baseDateTo,
            List<String> indexNames
    ) {
        // 여러 검색 이름으로 조회한 결과를 중복 없이 병합
        Map<ApiItemKey, Item> itemMap = new HashMap<>();

        for (String indexName : indexNames) {
            List<Item> items = fetchAllItemsByDateRange(baseDateFrom, baseDateTo, indexName);

            for (Item item : items) {
                // 같은 분류, 이름, 날짜 조합은 최초 1건만 사용
                itemMap.putIfAbsent(
                        new ApiItemKey(item.idxCsf(), item.idxNm(), item.basDt()),
                        item
                );
            }
        }

        return new ArrayList<>(itemMap.values());
    }

    private List<Item> extractItems(MarketIndexApiResponse response) {
        // 응답 구조가 비정상이면 빈 리스트 반환
        if (response == null || response.response() == null || response.response().body() == null) {
            return List.of();
        }
        return response.response().body().itemList();
    }

    private int extractTotalCount(MarketIndexApiResponse response) {
        // 응답 구조가 비정상이면 전체 건수를 0으로 처리
        if (response == null || response.response() == null || response.response().body() == null) {
            return 0;
        }

        String totalCount = response.response().body().totalCount();

        // totalCount 값이 없으면 전체 건수를 0으로 처리
        if (totalCount == null || totalCount.isBlank()) {
            return 0;
        }

        return Integer.parseInt(totalCount);
    }

    private int calculateTotalPages(int totalCount) {
        // 전체 건수가 0 이하면 조회할 페이지도 없음
        if (totalCount <= 0) {
            return 0;
        }
        return (totalCount + PAGE_SIZE - 1) / PAGE_SIZE;
    }

    private String findLatestAvailableBaseDate() {
        LocalDate date = LocalDate.now(KST);
        LocalDate lowerBound = date.minusDays(BASE_DATE_LOOKBACK_DAYS);

        while (!date.isBefore(lowerBound)) {
            String baseDate = formatBaseDate(date);
            MarketIndexApiResponse response = requestMarketIndex(baseDate, 1, 1);

            if (!extractItems(response).isEmpty()) {
                return baseDate;
            }

            // 데이터가 존재하지 않으면 하루 전으로 이동
            date = date.minusDays(1);
        }

        throw new BusinessLogicException(ExceptionCode.INDEX_SYNC_BASE_DATE_NOT_FOUND);
    }

    private String formatBaseDate(LocalDate baseDate) {
        return baseDate.format(BASIC_DATE_FORMATTER);
    }

    private MarketIndexApiResponse requestMarketIndex(String baseDate, int pageNo, int numOfRows) {
        MarketIndexApiRequest request = MarketIndexApiRequest.builder()
                .basDt(baseDate)
                .pageNo(pageNo)
                .numOfRows(numOfRows)
                .build();

        // Open API 호출
        return marketIndexApiClient.getMarketIndex(request);
    }

    private MarketIndexApiResponse requestMarketIndex(
            String beginDate,
            String endDate,
            String indexName,
            int pageNo
    ) {
        MarketIndexApiRequest request = MarketIndexApiRequest.builder()
                .beginBasDt(beginDate)
                .endBasDt(endDate)
                .idxNm(indexName)
                .pageNo(pageNo)
                .numOfRows(PAGE_SIZE)
                .build();

        // Open API 호출
        return marketIndexApiClient.getMarketIndex(request);
    }
}
