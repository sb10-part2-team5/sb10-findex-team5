package com.sprint.findex.scheduler;

import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@SpringBootTest(properties = "app.scheduler.sync-delay-ms=500")
@ActiveProfiles("test")
class AutoSyncSchedulerTest {

    @MockitoSpyBean
    private AutoSyncScheduler autoSyncScheduler;

    @Test
    @DisplayName("스케줄러가 설정된 주기로 실행된다")
    void scheduler_runsOnSchedule() throws InterruptedException {
        Thread.sleep(1500);
        verify(autoSyncScheduler, atLeast(2)).syncIndexData();
    }
}
