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
import org.springframework.stereotype.Service;

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
            IndexInfoLookupKey key = buildLookupKey(source.indexClassification(),
                    source.indexName());
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

    // 수동 연동
    public List<SyncJobDto> syncIndexData(IndexDataSyncRequest request, String worker) {
        LocalDate baseDateFrom = request.baseDateFrom();
        LocalDate baseDateTo = request.baseDateTo();
        // 날짜 검증
        if (baseDateFrom == null || baseDateTo == null || baseDateFrom.isAfter(baseDateTo)) {
            // 예외 개선 필요
            throw new RuntimeException();
        }

        return syncIndexDataInternal(request.indexInfoIds(), baseDateFrom, baseDateTo, worker);
    }

    // 자동 연동
    public void autoSyncIndexData(List<IndexDataSyncRequest> requests, String worker) {
        for (IndexDataSyncRequest request : requests) {
            LocalDate baseDateFrom = request.baseDateFrom();
            LocalDate baseDateTo = request.baseDateTo();

            // 연동 실행
            syncIndexDataInternal(request.indexInfoIds(), baseDateFrom, baseDateTo, worker);
        }
    }

    private List<SyncJobDto> syncIndexDataInternal(List<UUID> indexInfoIds, LocalDate baseDateFrom,
            LocalDate baseDateTo, String worker
    ) {
        List<IndexInfo> targetIndexInfos = resolveTargetIndexInfo(indexInfoIds);
        if (targetIndexInfos.isEmpty()) {
            return List.of();
        }

        List<Item> allItems = fetchAllItemsByDateRange(baseDateFrom, baseDateTo);
        if (allItems.isEmpty()) {
            return List.of();
        }

        Map<IndexInfoLookupKey, IndexInfo> targetIndexInfoMap = buildTargetIndexInfoMap(
                targetIndexInfos);
        Map<UUID, IndexInfo> targetIndexInfoById = new HashMap<>();
        for (IndexInfo indexInfo : targetIndexInfos) {
            targetIndexInfoById.put(indexInfo.getId(), indexInfo);
        }

        Map<IndexDataLookupKey, Item> itemMap = buildItemMap(allItems, targetIndexInfoMap);
        if (itemMap.isEmpty()) {
            return List.of();
        }

        List<UUID> targetIndexInfoIds = targetIndexInfos.stream()
                .map(IndexInfo::getId)
                .toList();
        Map<IndexDataLookupKey, IndexData> indexDataLookupMap = buildIndexDataLookupMap(
                targetIndexInfoIds,
                baseDateFrom,
                baseDateTo
        );

        List<SyncJobDto> syncJobs = new ArrayList<>();
        for (Map.Entry<IndexDataLookupKey, Item> entry : itemMap.entrySet()) {
            IndexDataLookupKey key = entry.getKey();
            IndexInfo indexInfo = targetIndexInfoById.get(key.indexInfoId());
            Item item = entry.getValue();
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
            } catch (Exception e) {
                try {
                    SyncJobDto failureJob = indexDataSyncFailureService.saveFailure(
                            indexInfo,
                            key.baseDate(),
                            worker,
                            jobTime,
                            e
                    );
                    syncJobs.add(failureJob);
                } catch (Exception ignored) {
                }
            }
        }

        return syncJobs;
    }

    private record IndexInfoLookupKey(String indexClassification, String standardName) {

    }

    private record IndexDataLookupKey(UUID indexInfoId, LocalDate baseDate) {

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

    private Map<IndexInfoLookupKey, IndexInfo> buildTargetIndexInfoMap(
            List<IndexInfo> targetIndexInfos) {
        Map<IndexInfoLookupKey, IndexInfo> targetIndexInfoMap = new HashMap<>();

        for (IndexInfo indexInfo : targetIndexInfos) {
            targetIndexInfoMap.put(
                    buildLookupKey(indexInfo.getIndexClassification(), indexInfo.getIndexName()),
                    indexInfo
            );
        }

        return targetIndexInfoMap;
    }

    private Map<IndexDataLookupKey, IndexData> buildIndexDataLookupMap(List<UUID> indexInfoIds,
            LocalDate baseDateFrom, LocalDate baseDateTo
    ) {
        if (indexInfoIds == null || indexInfoIds.isEmpty()) {
            return Map.of();
        }

        List<IndexData> existingIndexDataList = indexDataRepository.findAllByIndexInfoIdInAndBaseDateBetween(
                indexInfoIds,
                baseDateFrom,
                baseDateTo
        );

        Map<IndexDataLookupKey, IndexData> lookupMap = new HashMap<>();
        for (IndexData indexData : existingIndexDataList) {
            lookupMap.put(
                    new IndexDataLookupKey(indexData.getIndexInfo().getId(),
                            indexData.getBaseDate()),
                    indexData
            );
        }

        return lookupMap;
    }

    private Map<IndexDataLookupKey, Item> buildItemMap(List<Item> items,
            Map<IndexInfoLookupKey, IndexInfo> targetIndexInfoMap
    ) {
        Map<IndexDataLookupKey, Item> itemMap = new HashMap<>();

        for (Item item : items) {
            LocalDate baseDate = LocalDate.parse(item.basDt(), BASIC_DATE_FORMATTER);
            IndexInfoLookupKey key = buildLookupKey(item.idxCsf(), item.idxNm());
            IndexInfo indexInfo = targetIndexInfoMap.get(key);

            if (indexInfo == null) {
                continue;
            }

            itemMap.putIfAbsent(new IndexDataLookupKey(indexInfo.getId(), baseDate), item);
        }

        return itemMap;
    }

    private IndexInfoLookupKey buildLookupKey(String indexClassification, String indexName) {
        String normalizedIndexClassification = indexNameResolver.normalizeIndexClassification(
                indexClassification);

        // 변경된 지수명에 해당할 수 있으므로 확인
        String standardName = indexNameResolver.resolveStandardName(
                normalizedIndexClassification,
                indexName
        );

        return new IndexInfoLookupKey(normalizedIndexClassification, standardName);
    }

    private List<IndexInfo> resolveTargetIndexInfo(List<UUID> indexInfoIds) {
        if (indexInfoIds == null || indexInfoIds.isEmpty()) {
            // 스펙상 지수 정보 ID 목록이 비어있을 경우 모든 지수 대상
            // IndexDataSyncRequest 참고
            // OPEN_API 타입만 가져올지 고려
//            return indexInfoRepository.findAllBySourceType(SourceType.OPEN_API);
            return indexInfoRepository.findAll();
        }

        LinkedHashSet<UUID> requestedIds = new LinkedHashSet<>(indexInfoIds);
        List<IndexInfo> foundIndexInfos = indexInfoRepository.findAllById(requestedIds);

        // 요청 개수와 조회 개수가 같은지 확인
        if (foundIndexInfos.size() != requestedIds.size()) {
            // 예외 개선 필요
            throw new RuntimeException();
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
        String beginDate = formatBaseDate(baseDateFrom);
        String endDateExclusive = formatBaseDate(baseDateTo.plusDays(1));

        MarketIndexApiResponse firstPageResponse = requestMarketIndex(
                beginDate,
                endDateExclusive,
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
                    pageNo
            );
            allItems.addAll(extractItems(pageResponse));
        }

        return allItems;
    }

    private List<Item> extractItems(MarketIndexApiResponse response) {
        if (response == null || response.response() == null || response.response().body() == null) {
            return List.of();
        }
        return response.response().body().itemList();
    }

    private int extractTotalCount(MarketIndexApiResponse response) {
        if (response == null || response.response() == null || response.response().body() == null) {
            return 0;
        }

        String totalCount = response.response().body().totalCount();
        if (totalCount == null || totalCount.isBlank()) {
            return 0;
        }

        return Integer.parseInt(totalCount);
    }

    private int calculateTotalPages(int totalCount) {
        if (totalCount <= 0) {
            return 0;
        }
        return (totalCount + IndexSyncService.PAGE_SIZE - 1) / IndexSyncService.PAGE_SIZE;
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

        // 예외 개선 필요 - 데이터가 존재하는 기준 일자를 찾지 못함
        throw new RuntimeException();
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
            int pageNo
    ) {
        MarketIndexApiRequest request = MarketIndexApiRequest.builder()
                .beginBasDt(beginDate)
                .endBasDt(endDate)
                .pageNo(pageNo)
                .numOfRows(IndexSyncService.PAGE_SIZE)
                .build();

        // Open API 호출
        return marketIndexApiClient.getMarketIndex(request);
    }
}
