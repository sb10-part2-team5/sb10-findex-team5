package com.sprint.findex.repository;

import com.sprint.findex.entity.IndexData;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface IndexDataRepository extends JpaRepository<IndexData, UUID> {

    boolean existsByIndexInfo_IdAndBaseDate(UUID indexInfoId, LocalDate baseDate);

    @Query("SELECT d FROM IndexData d " +
            "WHERE (:indexInfoId IS NULL OR d.indexInfo.id = :indexInfoId) " +
            "AND (:startDate IS NULL OR d.baseDate >= :startDate) " +
            "AND (:endDate IS NULL OR d.baseDate <= :endDate)")
    List<IndexData> findAllForExport(
            @Param("indexInfoId") UUID indexInfoId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Sort sort);

    @Modifying
    @Query("delete from IndexData d where d.indexInfo.id = :indexInfoId")
    void deleteAllByIndexInfoId(UUID indexInfoId);
}
