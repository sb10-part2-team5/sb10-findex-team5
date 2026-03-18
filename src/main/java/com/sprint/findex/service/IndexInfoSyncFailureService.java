package com.sprint.findex.service;

import com.sprint.findex.dto.sync.IndexInfoLookup;
import com.sprint.findex.dto.sync.IndexInfoSyncSource;
import com.sprint.findex.dto.sync.SyncJobDto;
import com.sprint.findex.entity.IndexInfo;
import com.sprint.findex.entity.IntegrationTask;
import com.sprint.findex.enums.JobResult;
import com.sprint.findex.enums.JobType;
import com.sprint.findex.enums.SourceType;
import com.sprint.findex.mapper.SyncJobMapper;
import com.sprint.findex.repository.IndexInfoRepository;
import com.sprint.findex.repository.IntegrationTaskRepository;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class IndexInfoSyncFailureService {

    private final IndexInfoRepository indexInfoRepository;
    private final IntegrationTaskRepository integrationTaskRepository;
    private final SyncJobMapper syncJobMapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public SyncJobDto saveFailure(
            IndexInfoLookup lookup,
            IndexInfoSyncSource source,
            String standardName,
            String worker,
            Instant jobTime,
            String errorMessage
    ) {
        // IndexInfo 조회
        IndexInfo indexInfo = resolveFailureTarget(lookup, source, standardName);
        
        // 연동 실패 이력 저장
        IntegrationTask failedTask = integrationTaskRepository.save(
                IntegrationTask.create(
                        indexInfo,
                        JobType.INDEX_INFO.name(),
                        null,
                        worker,
                        jobTime,
                        JobResult.FAILED.name(),
                        errorMessage
                )
        );

        return syncJobMapper.toDto(failedTask);
    }

    private IndexInfo resolveFailureTarget(
            IndexInfoLookup lookup,
            IndexInfoSyncSource source,
            String standardName
    ) {
        if (lookup != null) {
            return indexInfoRepository.getReferenceById(lookup.id());
        }

        // lookup은 연동 작업 시작 전에 만들기 때문에 최신 상태라는 보장이 없음
        return indexInfoRepository.findByIndexClassificationAndIndexName(
                        source.indexClassification(), standardName
                )
                // 신규 지수 정보일 경우 생성
                .orElseGet(() -> indexInfoRepository.save(
                        IndexInfo.create(
                                standardName,
                                source.indexClassification(),
                                source.employedItemsCount(),
                                source.basePointInTime(),
                                source.baseIndex(),
                                SourceType.OPEN_API,
                                false
                        )
                ));
    }
}
