package com.sprint.findex.repository;

import com.sprint.findex.entity.AutoSyncConfig;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AutoSyncConfigRepository extends JpaRepository<AutoSyncConfig, UUID> {

}
