package com.sprint.findex.repository.dsl;

import com.sprint.findex.dto.indexinfo.CursorPageResponseIndexInfoDto;
import com.sprint.findex.dto.indexinfo.IndexInfoQueryCondition;

public interface IndexInfoCustomRepository {

  CursorPageResponseIndexInfoDto findAllWithIndexInfoQueryCondition(IndexInfoQueryCondition condition);
}
