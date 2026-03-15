package com.sprint.findex.controller;

import com.sprint.findex.dto.sync.SyncJobDto;
import com.sprint.findex.service.IndexSyncService;
import com.sprint.findex.util.ClientIpResolver;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sync-jobs")
public class IndexSyncController {

  private final IndexSyncService indexSyncService;
  private final ClientIpResolver clientIpResolver;

  @PostMapping("/index-infos")
  public ResponseEntity<List<SyncJobDto>> syncIndexInfos(HttpServletRequest request) {
    // 사용자 IP 추출
    String worker = clientIpResolver.resolve(request);
    List<SyncJobDto> syncJobDtos = indexSyncService.syncIndexInfos(worker);
    return ResponseEntity.accepted().body(syncJobDtos);
  }
}
