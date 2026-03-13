package com.sprint.findex.repository;


import com.sprint.findex.entity.IndexData;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.UUID;

public interface DashboardQueryRepository extends JpaRepository<IndexData, UUID> {
    List<IndexData> findAllByIndexInfoFavoriteTrue();
}
