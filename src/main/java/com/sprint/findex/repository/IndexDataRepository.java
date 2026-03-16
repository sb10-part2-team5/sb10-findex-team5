package com.sprint.findex.repository;

import com.sprint.findex.entity.IndexData;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;


public interface IndexDataRepository extends JpaRepository<IndexData, UUID> {

    boolean existsByIndexInfo_IdAndBaseDate(UUID indexInfoId, LocalDate baseDate);

    List<IndexData> findAllByIndexInfoIdAndCreatedAtBetween(UUID indexInfoId, LocalDate startDate,
            LocalDate endDate, Sort sort);
}
