package com.sprint.findex.repository;

import com.sprint.findex.entity.IntegrationTask;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IntegrationTaskRepository extends JpaRepository<IntegrationTask, UUID> {

    @Query("SELECT MAX(t.targetDate) FROM IntegrationTask t "
            + "WHERE t.indexInfo.id = :indexInfoId "
            + "AND t.jobType = 'INDEX_DATA' "
            + "AND t.result = 'SUCCESS'")
    Optional<LocalDate> findLastIndexDataSyncDate(
            @Param("indexInfoId") UUID indexInfoId
    );
}
