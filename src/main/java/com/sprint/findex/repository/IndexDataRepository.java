package com.sprint.findex.repository;

import com.sprint.findex.entity.IndexData;
import com.sprint.findex.repository.dsl.IndexDataCustomRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface IndexDataRepository extends JpaRepository<IndexData, UUID>,
        IndexDataCustomRepository {

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

    long countByIndexInfoId(UUID indexInfoId);

    List<IndexData> findAllByIndexInfoIdInAndBaseDateBetween(List<UUID> indexInfoIds,
            LocalDate baseDateFrom, LocalDate baseDateTo);

    @Query("""
            select data
            from IndexData data
            where data.indexInfo.id = :indexInfoId
              and data.baseDate = (
                  select max(latest.baseDate)
                  from IndexData latest
                  where latest.indexInfo.id = :indexInfoId
              )
            """)
    Optional<IndexData> findLatestByIndexInfoId(@Param("indexInfoId") UUID indexInfoId);

    @Query("""
            select data
            from IndexData data
            where data.indexInfo.id = :indexInfoId
              and data.baseDate >= :startDate
            order by data.baseDate desc
            """)
    List<IndexData> findChartDataByIndexInfoId(
            @Param("indexInfoId") UUID indexInfoId,
            @Param("startDate") LocalDate startDate
    );
}
