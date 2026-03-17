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

    //연동 이력 성공 데이터 중 indexInfoId 별 최신 날짜를 찾고 없다면 null 반환
    public List<IndexDataSyncRequest> buildAutoSyncTargets(List<UUID> indexInfoIds, LocalDate baseDateTo) {
        return indexInfoIds.stream()
                .map(id -> {
                    LocalDate baseDateFrom = integrationTaskRepository.findLastIndexDataSyncDate(id)
                            .map(lastDate -> lastDate.plusDays(1))
                            .orElse(null);
                    return new IndexDataSyncRequest(List.of(id), baseDateFrom, baseDateTo);
                })
                .filter(request -> request.baseDateFrom() == null || !request.baseDateFrom().isAfter(baseDateTo))
                .toList();
    }
}
