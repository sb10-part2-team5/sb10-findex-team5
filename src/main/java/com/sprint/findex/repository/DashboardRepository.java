package com.sprint.findex.repository;

import com.sprint.findex.entity.IndexData;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DashboardRepository extends JpaRepository<IndexData, UUID> {

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

    Optional<IndexData> findTopByIndexInfoIdAndBaseDateGreaterThanEqualOrderByBaseDateAsc(
            UUID indexInfoId,
            LocalDate baseDate
    );
}
