package com.sprint.findex.scheduler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.findex.dto.sync.AutoSyncTarget;
import com.sprint.findex.service.AutoSyncConfigService;
import com.sprint.findex.service.IndexSyncService;
import com.sprint.findex.service.IntegrationTaskService;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@SpringBootTest(properties = "app.scheduler.sync-delay-ms=500")
@ActiveProfiles("test")
class AutoSyncSchedulerTest {

    @MockitoSpyBean
    private AutoSyncScheduler autoSyncScheduler;

    @MockitoBean
    private AutoSyncConfigService autoSyncConfigService;

    @MockitoBean
    private IntegrationTaskService integrationTaskService;

    @MockitoBean
    private IndexSyncService indexSyncService;

    @Test
    @DisplayName("스케줄러가 설정된 주기로 실행된다")
    void scheduler_runsOnSchedule() throws InterruptedException {
        when(autoSyncConfigService.findEnabledIndexInfoIds()).thenReturn(List.of());

        Thread.sleep(1500);

        verify(autoSyncScheduler, atLeast(2)).syncIndexData();
    }

    @Test
    @DisplayName("활성화된 대상이 없으면 연동 서비스를 호출하지 않는다")
    void syncIndexData_noEnabledTargets_doesNotCallSyncService() {
        when(autoSyncConfigService.findEnabledIndexInfoIds()).thenReturn(List.of());

        autoSyncScheduler.syncIndexData();

        verify(integrationTaskService, never()).buildAutoSyncTargets(anyList());
        verify(indexSyncService, never()).syncIndexData(anyList(), any());
    }

    @Test
    @DisplayName("활성화된 대상이 있으면 연동 서비스를 호출한다")
    void syncIndexData_withEnabledTargets_callsSyncService() {
        UUID indexInfoId = UUID.randomUUID();
        AutoSyncTarget target = new AutoSyncTarget(indexInfoId, LocalDate.of(2026, 3, 11));

        when(autoSyncConfigService.findEnabledIndexInfoIds()).thenReturn(List.of(indexInfoId));
        when(integrationTaskService.buildAutoSyncTargets(List.of(indexInfoId))).thenReturn(List.of(target));

        autoSyncScheduler.syncIndexData();

        verify(integrationTaskService).buildAutoSyncTargets(eq(List.of(indexInfoId)));
        verify(indexSyncService).syncIndexData(eq(List.of(target)), any());
    }
}
