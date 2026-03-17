package com.sprint.findex.repository.dsl;

import com.sprint.findex.dto.indexinfo.IndexInfoDto;
import com.sprint.findex.dto.indexinfo.IndexInfoQueryCondition;
import com.sprint.findex.dto.response.PageResponse;

public interface IndexInfoCustomRepository {

  PageResponse<IndexInfoDto> findAllWithIndexInfoQueryCondition(IndexInfoQueryCondition condition);
}
