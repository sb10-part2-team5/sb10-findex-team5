package com.sprint.findex.repository;

import com.sprint.findex.entity.IntegrationTask;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IntegrationTaskRepository extends JpaRepository<IntegrationTask, UUID> {

}
