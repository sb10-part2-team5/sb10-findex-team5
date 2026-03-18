package com.sprint.findex.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.findex.dto.sync.IndexDataSyncRequest;
import com.sprint.findex.entity.IndexInfo;
import com.sprint.findex.entity.IntegrationTask;
import com.sprint.findex.enums.JobType;
import com.sprint.findex.enums.SourceType;
import com.sprint.findex.repository.IndexInfoRepository;
import com.sprint.findex.repository.IntegrationTaskRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class IntegrationTaskServiceTest {

    private static final LocalDate BASE_DATE_TO = LocalDate.of(2026, 3, 16);

    @Autowired
    private IntegrationTaskService integrationTaskService;

    @Autowired
    private IndexInfoRepository indexInfoRepository;

    @Autowired
    private IntegrationTaskRepository integrationTaskRepository;

    @Test
    @DisplayName("자동 연동 대상 생성 - 마지막 성공일이 있으면 baseDateFrom은 다음날")
    void buildAutoSyncTargets_withLastSyncDate_returnsNextDay() {
        IndexInfo indexInfo = indexInfoRepository.save(createIndexInfo("KOSPI"));
        saveSuccessTask(indexInfo, LocalDate.of(2026, 3, 10));

        List<IndexDataSyncRequest> targets = integrationTaskService.buildAutoSyncTargets(
            List.of(indexInfo.getId()), BASE_DATE_TO
        );

        assertThat(targets).hasSize(1);
        assertThat(targets.get(0).indexInfoIds()).containsExactly(indexInfo.getId());
        assertThat(targets.get(0).baseDateFrom()).isEqualTo(LocalDate.of(2026, 3, 11));
        assertThat(targets.get(0).baseDateTo()).isEqualTo(BASE_DATE_TO);
    }

    @Test
    @DisplayName("자동 연동 대상 생성 - 연동 이력이 없으면 baseDateTo 하루만 요청")
    void buildAutoSyncTargets_withNoHistory_requestsOnlyBaseDateTo() {
        IndexInfo indexInfo = indexInfoRepository.save(createIndexInfo("KOSPI"));

        List<IndexDataSyncRequest> targets = integrationTaskService.buildAutoSyncTargets(
            List.of(indexInfo.getId()), BASE_DATE_TO
        );

        assertThat(targets).hasSize(1);
        assertThat(targets.get(0).indexInfoIds()).containsExactly(indexInfo.getId());
        assertThat(targets.get(0).baseDateFrom()).isEqualTo(BASE_DATE_TO);
        assertThat(targets.get(0).baseDateTo()).isEqualTo(BASE_DATE_TO);
    }

    @Test
    @DisplayName("자동 연동 대상 생성 - 실패 이력만 있으면 baseDateFrom은 baseDateTo")
    void buildAutoSyncTargets_withOnlyFailures_returnsBaseDateToAsBaseDateFrom() {
        IndexInfo indexInfo = indexInfoRepository.save(createIndexInfo("KOSPI"));
        integrationTaskRepository.save(IntegrationTask.create(
            indexInfo, JobType.INDEX_DATA.name(), LocalDate.of(2026, 3, 10),
            "SYSTEM", Instant.now(), "FAILURE", "에러 발생"
        ));

        List<IndexDataSyncRequest> targets = integrationTaskService.buildAutoSyncTargets(
            List.of(indexInfo.getId()), BASE_DATE_TO
        );

        assertThat(targets).hasSize(1);
        assertThat(targets.get(0).baseDateFrom()).isEqualTo(BASE_DATE_TO);
        assertThat(targets.get(0).baseDateTo()).isEqualTo(BASE_DATE_TO);
    }

    @Test
    @DisplayName("자동 연동 대상 생성 - 여러 지수의 baseDateFrom을 각각 계산")
    void buildAutoSyncTargets_multipleIds_returnsEachBaseDateFrom() {
        IndexInfo kospi = indexInfoRepository.save(createIndexInfo("KOSPI"));
        IndexInfo kosdaq = indexInfoRepository.save(createIndexInfo("KOSDAQ"));
        saveSuccessTask(kospi, LocalDate.of(2026, 3, 10));
        saveSuccessTask(kosdaq, LocalDate.of(2026, 3, 12));

        List<IndexDataSyncRequest> targets = integrationTaskService.buildAutoSyncTargets(
            List.of(kospi.getId(), kosdaq.getId()), BASE_DATE_TO
        );

        assertThat(targets).hasSize(2);
        assertThat(targets).anySatisfy(t -> {
            assertThat(t.indexInfoIds()).containsExactly(kospi.getId());
            assertThat(t.baseDateFrom()).isEqualTo(LocalDate.of(2026, 3, 11));
        });
        assertThat(targets).anySatisfy(t -> {
            assertThat(t.indexInfoIds()).containsExactly(kosdaq.getId());
            assertThat(t.baseDateFrom()).isEqualTo(LocalDate.of(2026, 3, 13));
        });
    }

    @Test
    @DisplayName("자동 연동 대상 생성 - 성공 이력과 실패 이력이 혼재하면 성공 이력 기준으로 baseDateFrom 계산")
    void buildAutoSyncTargets_mixedResults_usesSuccessDate() {
        IndexInfo indexInfo = indexInfoRepository.save(createIndexInfo("KOSPI"));
        saveSuccessTask(indexInfo, LocalDate.of(2026, 3, 10));
        integrationTaskRepository.save(IntegrationTask.create(
            indexInfo, JobType.INDEX_DATA.name(), LocalDate.of(2026, 3, 15),
            "SYSTEM", Instant.now(), "FAILURE", "에러 발생"
        ));

        List<IndexDataSyncRequest> targets = integrationTaskService.buildAutoSyncTargets(
            List.of(indexInfo.getId()), BASE_DATE_TO
        );

        assertThat(targets.get(0).baseDateFrom()).isEqualTo(LocalDate.of(2026, 3, 11));
    }

    @Test
    @DisplayName("자동 연동 대상 생성 - 사용자가 오늘 수동 갱신 후 자정에 스케줄러 실행 시 오늘~오늘 범위로 요청")
    void buildAutoSyncTargets_manualSyncToday_requestsTodayOnly() {
        // 사용자가 오늘(3/17) 어제(3/16) 데이터를 수동 연동
        // 자정이 지나 스케줄러 실행 → baseDateTo = 3/18 - 1 = 3/17 (오늘)
        LocalDate today = LocalDate.of(2026, 3, 17);
        LocalDate yesterday = today.minusDays(1);
        IndexInfo indexInfo = indexInfoRepository.save(createIndexInfo("KOSPI"));
        saveSuccessTask(indexInfo, yesterday);  // 마지막 성공 targetDate = 3/16

        List<IndexDataSyncRequest> targets = integrationTaskService.buildAutoSyncTargets(
            List.of(indexInfo.getId()), today  // baseDateTo = 3/17
        );

        assertThat(targets.get(0).baseDateFrom()).isEqualTo(today);  // 3/16 + 1 = 3/17
        assertThat(targets.get(0).baseDateTo()).isEqualTo(today);
    }

    @Test
    @DisplayName("자동 연동 대상 생성 - 오늘 데이터를 이미 연동했어도 오늘 데이터를 다시 요청")
    void buildAutoSyncTargets_alreadySyncedToday_requestsTodayAgain() {
        LocalDate today = LocalDate.of(2026, 3, 17);
        IndexInfo indexInfo = indexInfoRepository.save(createIndexInfo("KOSPI"));
        saveSuccessTask(indexInfo, today);  // 오늘 데이터까지 이미 연동됨

        List<IndexDataSyncRequest> targets = integrationTaskService.buildAutoSyncTargets(
            List.of(indexInfo.getId()), today
        );

        assertThat(targets).hasSize(1);
        assertThat(targets.get(0).baseDateFrom()).isEqualTo(today);
        assertThat(targets.get(0).baseDateTo()).isEqualTo(today);
    }

    @Test
    @DisplayName("자동 연동 대상 생성 - 미래로 넘어간 다음날 대신 오늘을 다시 요청")
    void buildAutoSyncTargets_lastSuccessAfterBaseDateTo_requestsToday() {
        LocalDate today = LocalDate.of(2026, 3, 17);
        IndexInfo indexInfo = indexInfoRepository.save(createIndexInfo("KOSPI"));
        saveSuccessTask(indexInfo, today.plusDays(1));

        List<IndexDataSyncRequest> targets = integrationTaskService.buildAutoSyncTargets(
            List.of(indexInfo.getId()), today
        );

        assertThat(targets).hasSize(1);
        assertThat(targets.get(0).baseDateFrom()).isEqualTo(today);
        assertThat(targets.get(0).baseDateTo()).isEqualTo(today);
    }

    @Test
    @DisplayName("자동 연동 대상 생성 - 빈 목록이면 빈 결과 반환")
    void buildAutoSyncTargets_emptyInput_returnsEmptyList() {
        List<IndexDataSyncRequest> targets = integrationTaskService.buildAutoSyncTargets(List.of(), BASE_DATE_TO);

        assertThat(targets).isEmpty();
    }

    private void saveSuccessTask(IndexInfo indexInfo, LocalDate targetDate) {
        integrationTaskRepository.save(IntegrationTask.create(
            indexInfo, JobType.INDEX_DATA.name(), targetDate, "SYSTEM", Instant.now(), "SUCCESS"
        ));
    }

    private IndexInfo createIndexInfo(String indexName) {
        return IndexInfo.create(
            indexName, "KOSPI시리즈", 42,
            LocalDate.of(2024, 1, 2), new BigDecimal("1000.1234"),
            SourceType.USER, false
        );
    }
}
