package com.sprint.findex.repository;

import com.sprint.findex.entity.IndexData;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DashboardRepository extends JpaRepository<IndexData, UUID> {

    @EntityGraph(attributePaths = "indexInfo")
    @Query("""
            select data
            from IndexData data
            join data.indexInfo indexInfo
            where data.baseDate = (
                  select max(latest.baseDate)
                  from IndexData latest
                  where latest.indexInfo.id = indexInfo.id
              )
            order by indexInfo.indexClassification, indexInfo.indexName
            """)
    List<IndexData> findLatestIndexData();

    @EntityGraph(attributePaths = "indexInfo")
    @Query("""
            select data
            from IndexData data
            join data.indexInfo indexInfo
            where indexInfo.favorite = true
              and data.baseDate = (
                  select max(latest.baseDate)
                  from IndexData latest
                  where latest.indexInfo.id = indexInfo.id
              )
            order by indexInfo.indexClassification, indexInfo.indexName
            """)
    List<IndexData> findLatestFavoriteIndexData();

    @Query("""
            select data
            from IndexData data
            where data.indexInfo.id = :indexInfoId
              and data.baseDate = (
                  select min(candidate.baseDate)
                  from IndexData candidate
                  where candidate.indexInfo.id = :indexInfoId
                    and candidate.baseDate >= :baseDate
              )
            """)
    Optional<IndexData> findNearestByIndexInfoIdFromBaseDate(
            @Param("indexInfoId") UUID indexInfoId,
            @Param("baseDate") LocalDate baseDate
    );


}
