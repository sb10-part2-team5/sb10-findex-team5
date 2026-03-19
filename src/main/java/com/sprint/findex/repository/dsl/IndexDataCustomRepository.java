package com.sprint.findex.repository.dsl;

import com.sprint.findex.dto.indexdata.IndexDataDto;
import com.sprint.findex.dto.indexdata.IndexDataQueryCondition;
import com.sprint.findex.entity.IndexData;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Sort;

public interface IndexDataCustomRepository {

    List<IndexDataDto> findAllWithIndexDataQueryCondition(IndexDataQueryCondition condition);

    List<IndexData> findAllForExport(UUID indexInfoId, LocalDate startDate, LocalDate endDate,
            Sort sort);
}
