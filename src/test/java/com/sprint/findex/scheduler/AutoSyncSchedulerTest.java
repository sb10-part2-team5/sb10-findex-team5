package com.sprint.findex.scheduler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.findex.dto.sync.IndexDataSyncRequest;
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

@SpringBootTest(properties = "app.scheduler.fixed-delay-ms=1000")
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

        Thread.sleep(2500);

        verify(autoSyncScheduler, atLeast(2)).syncIndexData();
    }

    @Test
    @DisplayName("활성화된 대상이 없으면 연동 서비스를 호출하지 않는다")
    void syncIndexData_noEnabledTargets_doesNotCallSyncService() {
        when(autoSyncConfigService.findEnabledIndexInfoIds()).thenReturn(List.of());

        autoSyncScheduler.syncIndexData();

        verify(integrationTaskService, never()).buildAutoSyncTargets(anyList(), any());
        verify(indexSyncService, never()).autoSyncIndexData(anyList(), any());
    }

    @Test
    @DisplayName("활성화된 대상이 있으면 오늘 기준으로 연동 서비스를 호출한다")
    void syncIndexData_withEnabledTargets_callsSyncServiceWithToday() {
        UUID indexInfoId = UUID.randomUUID();
        IndexDataSyncRequest request = new IndexDataSyncRequest(List.of(indexInfoId), null, null);

        when(autoSyncConfigService.findEnabledIndexInfoIds()).thenReturn(List.of(indexInfoId));
        when(integrationTaskService.buildAutoSyncTargets(eq(List.of(indexInfoId)), any(LocalDate.class)))
            .thenReturn(List.of(request));

        autoSyncScheduler.syncIndexData();

        verify(integrationTaskService).buildAutoSyncTargets(eq(List.of(indexInfoId)), any(LocalDate.class));
        verify(indexSyncService).autoSyncIndexData(eq(List.of(request)), any());
    }
}
