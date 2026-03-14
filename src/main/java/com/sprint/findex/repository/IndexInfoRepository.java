package com.sprint.findex.repository;

import com.sprint.findex.entity.IndexInfo;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IndexInfoRepository extends JpaRepository<IndexInfo, UUID> {

  boolean existsByIndexClassificationAndIndexName(String indexClassification, String IndexName);

  Optional<IndexInfo> findByIndexClassificationAndIndexName(String indexClassification,
      String indexName);
}
