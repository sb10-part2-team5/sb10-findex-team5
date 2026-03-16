package com.sprint.findex.repository.dsl;

import com.sprint.findex.dto.autosyncconfig.AutoSyncConfigDto;
import com.sprint.findex.dto.autosyncconfig.AutoSyncConfigQueryCondition;
import com.sprint.findex.dto.response.PageResponse;

public interface AutoSyncConfigCustomRepository {

    PageResponse<AutoSyncConfigDto> findAllWithCondition(AutoSyncConfigQueryCondition condition);
}
