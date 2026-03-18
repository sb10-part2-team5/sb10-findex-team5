package com.sprint.findex.service;

import com.sprint.findex.dto.response.PageResponse;
import com.sprint.findex.dto.sync.IndexDataSyncRequest;
import com.sprint.findex.dto.sync.SyncJobDto;
import com.sprint.findex.dto.sync.SyncJobQueryCondition;
import com.sprint.findex.entity.IntegrationTask;
import com.sprint.findex.mapper.SyncJobMapper;
import com.sprint.findex.repository.IntegrationTaskRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IntegrationTaskService {

    private final IntegrationTaskRepository integrationTaskRepository;
    private final SyncJobMapper syncJobMapper;

    public List<IndexDataSyncRequest> buildAutoSyncTargets(List<UUID> indexInfoIds,
            LocalDate baseDateTo) {
        return indexInfoIds.stream()
                .map(id -> buildAutoSyncTarget(id, baseDateTo))
                .toList();
    }

    private IndexDataSyncRequest buildAutoSyncTarget(UUID id, LocalDate baseDateTo) {
        LocalDate baseDateFrom = integrationTaskRepository.findLastIndexDataSyncDate(id)
                .map(lastDate -> {
                    LocalDate nextUnsyncedDate = lastDate.plusDays(1);
                    return nextUnsyncedDate.isAfter(baseDateTo) ? baseDateTo : nextUnsyncedDate;
                })
                .orElse(baseDateTo);

        return new IndexDataSyncRequest(List.of(id), baseDateFrom, baseDateTo);
    }

    public PageResponse<SyncJobDto> getSyncJobList(SyncJobQueryCondition condition) {
        PageResponse<IntegrationTask> page = integrationTaskRepository.findAllWithSyncJobQueryCondition(
                condition);

        List<SyncJobDto> content = page.content().stream()
                .map(syncJobMapper::toDto)
                .toList();

        if (shouldReturnPlaceholderForStats(condition, content)) {
            content = List.of(new SyncJobDto(
                    UUID.randomUUID(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            ));
        }

        return new PageResponse<SyncJobDto>(
                content,
                page.nextCursor(),
                page.nextIdAfter(),
                page.size(),
                page.totalElements(),
                page.hasNext()
        );
    }

    private boolean shouldReturnPlaceholderForStats(SyncJobQueryCondition condition,
            List<SyncJobDto> content) {
        return content.isEmpty()
                && condition.cursor() == null
                && condition.idAfter() == null
                && condition.status() != null
                && condition.jobTimeFrom() != null
                && condition.jobTimeTo() != null
                && condition.jobType() == null
                && condition.indexInfoId() == null
                && condition.worker() == null
                && condition.baseDateFrom() == null
                && condition.baseDateTo() == null;
    }
}
