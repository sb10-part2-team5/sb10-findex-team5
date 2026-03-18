package com.sprint.findex.controller;

import com.sprint.findex.dto.response.PageResponse;
import com.sprint.findex.dto.sync.IndexDataSyncRequest;
import com.sprint.findex.dto.sync.SyncJobDto;
import com.sprint.findex.dto.sync.SyncJobQueryCondition;
import com.sprint.findex.service.IndexSyncService;
import com.sprint.findex.service.IntegrationTaskService;
import com.sprint.findex.util.ClientIpResolver;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sync-jobs")
public class IndexSyncController {

    private final IndexSyncService indexSyncService;
    private final IntegrationTaskService integrationTaskService;
    private final ClientIpResolver clientIpResolver;

    @PostMapping("/index-infos")
    public ResponseEntity<List<SyncJobDto>> syncIndexInfo(HttpServletRequest request) {
        // 사용자 IP 추출
        String worker = clientIpResolver.resolve(request);
        List<SyncJobDto> syncJobDtos = indexSyncService.syncIndexInfo(worker);
        return ResponseEntity.accepted().body(syncJobDtos);
    }

    @PostMapping("/index-data")
    public ResponseEntity<List<SyncJobDto>> syncIndexData(
            @Valid @RequestBody IndexDataSyncRequest indexDataSyncRequest,
            HttpServletRequest request
    ) {
        String worker = clientIpResolver.resolve(request);
        List<SyncJobDto> syncJobDtos = indexSyncService.syncIndexData(indexDataSyncRequest, worker);
        return ResponseEntity.accepted().body(syncJobDtos);
    }

    @GetMapping
    public ResponseEntity<PageResponse<SyncJobDto>> getSyncJobList(
            @ParameterObject @Valid @ModelAttribute SyncJobQueryCondition condition
    ) {
        return ResponseEntity.ok(integrationTaskService.getSyncJobList(condition));
    }
}
