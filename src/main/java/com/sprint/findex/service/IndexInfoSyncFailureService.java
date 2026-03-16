package com.sprint.findex.service;

import com.sprint.findex.dto.sync.IndexInfoLookup;
import com.sprint.findex.dto.sync.SyncJobDto;
import com.sprint.findex.entity.IndexInfo;
import com.sprint.findex.entity.IntegrationTask;
import com.sprint.findex.enums.JobResult;
import com.sprint.findex.enums.JobType;
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
            String worker,
            Instant jobTime,
            Exception e
    ) {
        // 신규 지수 정보라는 의미
        // 정책 변경 혹은 개선 필요
        if (lookup == null) {
            return null;
        }

        // IndexInfo 조회
        IndexInfo indexInfo = indexInfoRepository.getReferenceById(lookup.id());
        // 연동 실패 이력 저장
        IntegrationTask failedTask = integrationTaskRepository.save(
                IntegrationTask.create(
                        indexInfo,
                        JobType.INDEX_INFO.name(),
                        null,
                        worker,
                        jobTime,
                        JobResult.FAILED.name(),
                        e.getMessage()
                )
        );

        return syncJobMapper.toDto(failedTask);
    }
}
