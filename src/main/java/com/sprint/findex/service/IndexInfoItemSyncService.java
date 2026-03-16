package com.sprint.findex.service;

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
import com.sprint.findex.util.IndexNameResolver;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class IndexInfoItemSyncService {

    private final IndexInfoRepository indexInfoRepository;
    private final IntegrationTaskRepository integrationTaskRepository;
    private final MarketIndexApiSyncMapper marketIndexApiSyncMapper;
    private final SyncJobMapper syncJobMapper;
    private final IndexNameResolver indexNameResolver;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public SyncJobDto syncSingleItem(Item item, String worker, Instant jobTime) {
        try {
            // IndexInfo 업데이트 또는 생성
            IndexInfo indexInfo = updateOrCreateIndexInfo(item);

            // 연동 성공 이력 저장
            IntegrationTask savedTask = integrationTaskRepository.save(
                    IntegrationTask.create(
                            indexInfo,
                            JobType.INDEX_INFO.name(),
                            null,
                            worker,
                            jobTime,
                            JobResult.SUCCESS.name()
                    )
            );

            return syncJobMapper.toDto(savedTask);
        } catch (Exception e) {
            // 연동 실패 시 실패 이력 저장 시도
            IntegrationTask failedTask = saveFailureTask(item, worker, jobTime, e);
            if (failedTask != null) {
                return syncJobMapper.toDto(failedTask);
            }
            // 예외 개선 필요 - 연동 실패 및 실패 이력 저장 실패
            // 실패 이력 저장도 실패 시 예외 반환
            throw new RuntimeException(e);
        }
    }

    private IndexInfo updateOrCreateIndexInfo(Item item) {
        // Open API 응답 데이터를 IndexInfoSyncSource 객체로 변환
        IndexInfoSyncSource source = marketIndexApiSyncMapper.toSource(item);
        // 변경된 지수명에 해당할 수 있으므로 확인
        String standardName = indexNameResolver.resolveStandardName(
                source.indexClassification(),
                source.indexName()
        );

        // 기존 IndexInfo가 존재하는지 조회
        Optional<IndexInfo> existingOptional = findExistingIndexInfo(
                source.indexClassification(),
                standardName
        );

        // 기존 데이터가 없는 경우 새로운 IndexInfo 생성
        if (existingOptional.isEmpty()) {
            return indexInfoRepository.save(
                    IndexInfo.create(
                            standardName,
                            source.indexClassification(),
                            source.employedItemsCount(),
                            source.basePointInTime(),
                            source.baseIndex(),
                            SourceType.OPEN_API,
                            false
                    )
            );
        }

        IndexInfo existing = existingOptional.get();
        if (existing.getSourceType() != SourceType.OPEN_API) {
            // 예외 개선 필요 - OPEN_API가 아님
            throw new RuntimeException();
        }

        // 기존 IndexInfo 데이터 최신화
        existing.updateIndexInfo(
                source.employedItemsCount(),
                source.basePointInTime(),
                source.baseIndex(),
                existing.getFavorite()
        );

        return existing;
    }

    private Optional<IndexInfo> findExistingIndexInfo(String indexClassification,
            String standardName) {
        // 조회 가능한 이름 목록 생성
        List<String> searchNames = indexNameResolver.buildSearchNames(
                indexClassification,
                standardName
        );

        // 이름 후보를 순서대로 조회
        for (String name : searchNames) {
            Optional<IndexInfo> found = indexInfoRepository.findByIndexClassificationAndIndexName(
                    indexClassification,
                    name
            );

            if (found.isPresent()) {
                return found;
            }
        }

        return Optional.empty();
    }

    private IntegrationTask saveFailureTask(Item item, String worker, Instant jobTime,
            Exception e) {
        IndexInfoSyncSource source = marketIndexApiSyncMapper.toSource(item);
        // 변경된 지수명에 해당할 수 있으므로 확인
        String standardName = indexNameResolver.resolveStandardName(
                source.indexClassification(),
                source.indexName()
        );

        // 기존 IndexInfo가 존재하는지 조회
        Optional<IndexInfo> existingOptional = findExistingIndexInfo(
                source.indexClassification(),
                standardName
        );

        // 실패 이력 생성 및 저장
        return existingOptional.map(indexInfo -> integrationTaskRepository.save(
                IntegrationTask.create(
                        indexInfo,
                        JobType.INDEX_INFO.name(),
                        null,
                        worker,
                        jobTime,
                        JobResult.FAILED.name(),
                        // 실패 메시지 개선 필요
                        e.getMessage()
                )
        )).orElse(null);
    }
}