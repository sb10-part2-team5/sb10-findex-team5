package com.sprint.findex.repository;

import com.sprint.findex.entity.AutoSyncConfig;
import com.sprint.findex.repository.dsl.AutoSyncConfigCustomRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AutoSyncConfigRepository
        extends JpaRepository<AutoSyncConfig, UUID>, AutoSyncConfigCustomRepository {

    List<AutoSyncConfig> findAllByEnabledTrue();
}
