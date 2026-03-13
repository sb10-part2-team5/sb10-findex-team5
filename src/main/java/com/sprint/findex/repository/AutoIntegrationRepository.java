package com.sprint.findex.repository;

import com.sprint.findex.entity.AutoIntegration;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AutoIntegrationRepository extends JpaRepository<AutoIntegration, UUID> {

}
