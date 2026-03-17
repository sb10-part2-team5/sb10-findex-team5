package com.sprint.findex.repository.dsl;

import com.sprint.findex.dto.response.PageResponse;
import com.sprint.findex.dto.sync.SyncJobQueryCondition;
import com.sprint.findex.entity.IntegrationTask;

public interface IntegrationTaskCustomRepository {

    PageResponse<IntegrationTask> findAllWithSyncJobQueryCondition(SyncJobQueryCondition condition);

    long countWithSyncJobQueryCondition(SyncJobQueryCondition condition);
}
