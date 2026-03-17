package com.sprint.findex.repository.dsl;

import com.sprint.findex.dto.indexdata.IndexDataDto;
import com.sprint.findex.dto.indexdata.IndexDataQueryCondition;
import java.util.List;

public interface IndexDataCustomRepository {

    List<IndexDataDto> findAllWithIndexDataQueryCondition(IndexDataQueryCondition condition);
}
