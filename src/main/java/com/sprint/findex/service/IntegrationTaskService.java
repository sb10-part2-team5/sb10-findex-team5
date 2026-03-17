package com.sprint.findex.service;

import com.sprint.findex.dto.sync.IndexDataSyncRequest;
import com.sprint.findex.repository.IntegrationTaskRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IntegrationTaskService {

    private final IntegrationTaskRepository integrationTaskRepository;

    public List<IndexDataSyncRequest> buildAutoSyncTargets(List<UUID> indexInfoIds, LocalDate baseDateTo) {
        return indexInfoIds.stream()
                .map(id -> {
                    LocalDate baseDateFrom = integrationTaskRepository.findLastIndexDataSyncDate(id)
                            .map(lastDate -> lastDate.plusDays(1))
                            .orElse(baseDateTo);

                    return new IndexDataSyncRequest(List.of(id), baseDateFrom, baseDateTo);
                })
                .filter(request -> request.baseDateFrom() == null || !request.baseDateFrom().isAfter(baseDateTo))
                .toList();
    }
}
