package com.sprint.findex.service;

import com.sprint.findex.dto.sync.SyncJobDto;
import com.sprint.findex.entity.IndexInfo;
import com.sprint.findex.entity.IntegrationTask;
import com.sprint.findex.enums.JobResult;
import com.sprint.findex.enums.JobType;
import com.sprint.findex.mapper.SyncJobMapper;
import com.sprint.findex.repository.IntegrationTaskRepository;
import java.time.Instant;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class IndexDataSyncFailureService {

    private final IntegrationTaskRepository integrationTaskRepository;
    private final SyncJobMapper syncJobMapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public SyncJobDto saveFailure(
            IndexInfo indexInfo,
            LocalDate targetDate,
            String worker,
            Instant jobTime,
            Exception e
    ) {
        // 연동 실패 이력 저장
        IntegrationTask failedTask = integrationTaskRepository.save(
                IntegrationTask.create(
                        indexInfo,
                        JobType.INDEX_DATA.name(),
                        targetDate,
                        worker,
                        jobTime,
                        JobResult.FAILED.name(),
                        // 실패 메시지 개선 필요
                        e.getMessage()
                )
        );

        return syncJobMapper.toDto(failedTask);
    }
}
