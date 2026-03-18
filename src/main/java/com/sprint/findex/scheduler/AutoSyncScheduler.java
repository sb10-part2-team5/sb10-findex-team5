package com.sprint.findex.scheduler;

import com.sprint.findex.dto.sync.IndexDataSyncRequest;
import com.sprint.findex.service.AutoSyncConfigService;
import com.sprint.findex.service.IndexSyncService;
import com.sprint.findex.service.IntegrationTaskService;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AutoSyncScheduler {

    private static final String WORKER_NAME = "SYSTEM";
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private final AutoSyncConfigService autoSyncConfigService;
    private final IntegrationTaskService integrationTaskService;
    private final IndexSyncService indexSyncService;

    @Scheduled(fixedDelayString = "${app.scheduler.fixed-delay-ms}")
    public void syncIndexData() {
        log.info("자동 연동 스케줄러 실행");
        try {
            List<UUID> indexInfoIds = autoSyncConfigService.findEnabledIndexInfoIds();

            if (indexInfoIds.isEmpty()) {
                log.info("활성화된 자동 연동 대상이 없습니다.");
                return;
            }

            LocalDate today = LocalDate.now(KST);
            List<IndexDataSyncRequest> requests = integrationTaskService.buildAutoSyncTargets(indexInfoIds, today);

            log.info("자동 연동 대상: {}건", requests.size());
            indexSyncService.autoSyncIndexData(requests, WORKER_NAME);
            log.info("자동 연동 스케줄러 완료");
        } catch (Exception e) {
            log.error("자동 연동 스케줄러 실패", e);
        }
    }
}
