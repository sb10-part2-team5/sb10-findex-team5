package com.sprint.findex.repository;

import com.sprint.findex.dto.indexinfo.IndexInfoSummaryDto;
import com.sprint.findex.entity.IndexInfo;
import com.sprint.findex.repository.dsl.IndexInfoCustomRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IndexInfoRepository extends JpaRepository<IndexInfo, UUID>,
    IndexInfoCustomRepository {

    boolean existsByIndexClassificationAndIndexName(String indexClassification, String IndexName);

    Optional<IndexInfo> findByIndexClassificationAndIndexName(String indexClassification,
        String indexName);

    @Query("SELECT new com.sprint.findex.dto.indexinfo.IndexInfoSummaryDto(i.id, i.indexName, i.indexClassification ) "
        + "FROM IndexInfo i")
    List<IndexInfoSummaryDto> findAllSummaries();
}
