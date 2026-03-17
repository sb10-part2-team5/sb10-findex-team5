package com.sprint.findex.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.findex.dto.sync.AutoSyncTarget;
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
import java.util.UUID;
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

    @Autowired
    private IntegrationTaskService integrationTaskService;

    @Autowired
    private IndexInfoRepository indexInfoRepository;

    @Autowired
    private IntegrationTaskRepository integrationTaskRepository;

    @Test
    @DisplayName("자동 연동 대상 생성 - 마지막 성공일이 있으면 startDate는 다음날")
    void buildAutoSyncTargets_withLastSyncDate_returnsNextDay() {
        IndexInfo indexInfo = indexInfoRepository.save(createIndexInfo("KOSPI"));
        saveSuccessTask(indexInfo, LocalDate.of(2026, 3, 10));

        List<AutoSyncTarget> targets = integrationTaskService.buildAutoSyncTargets(List.of(indexInfo.getId()));

        assertThat(targets).hasSize(1);
        assertThat(targets.get(0).indexInfoId()).isEqualTo(indexInfo.getId());
        assertThat(targets.get(0).startDate()).isEqualTo(LocalDate.of(2026, 3, 11));
    }

    @Test
    @DisplayName("자동 연동 대상 생성 - 성공 이력이 없으면 startDate는 null")
    void buildAutoSyncTargets_withNoHistory_returnsNullStartDate() {
        IndexInfo indexInfo = indexInfoRepository.save(createIndexInfo("KOSPI"));

        List<AutoSyncTarget> targets = integrationTaskService.buildAutoSyncTargets(List.of(indexInfo.getId()));

        assertThat(targets).hasSize(1);
        assertThat(targets.get(0).indexInfoId()).isEqualTo(indexInfo.getId());
        assertThat(targets.get(0).startDate()).isNull();
    }

    @Test
    @DisplayName("자동 연동 대상 생성 - 실패 이력만 있으면 startDate는 null")
    void buildAutoSyncTargets_withOnlyFailures_returnsNullStartDate() {
        IndexInfo indexInfo = indexInfoRepository.save(createIndexInfo("KOSPI"));
        integrationTaskRepository.save(IntegrationTask.create(
            indexInfo, JobType.INDEX_DATA.name(), LocalDate.of(2026, 3, 10),
            "SYSTEM", Instant.now(), "FAILURE", "에러 발생"
        ));

        List<AutoSyncTarget> targets = integrationTaskService.buildAutoSyncTargets(List.of(indexInfo.getId()));

        assertThat(targets.get(0).startDate()).isNull();
    }

    @Test
    @DisplayName("자동 연동 대상 생성 - 여러 지수의 startDate를 각각 계산")
    void buildAutoSyncTargets_multipleIds_returnsEachStartDate() {
        IndexInfo kospi = indexInfoRepository.save(createIndexInfo("KOSPI"));
        IndexInfo kosdaq = indexInfoRepository.save(createIndexInfo("KOSDAQ"));
        saveSuccessTask(kospi, LocalDate.of(2026, 3, 10));
        saveSuccessTask(kosdaq, LocalDate.of(2026, 3, 12));

        List<AutoSyncTarget> targets = integrationTaskService.buildAutoSyncTargets(
            List.of(kospi.getId(), kosdaq.getId())
        );

        assertThat(targets).hasSize(2);
        assertThat(targets).anySatisfy(t -> {
            assertThat(t.indexInfoId()).isEqualTo(kospi.getId());
            assertThat(t.startDate()).isEqualTo(LocalDate.of(2026, 3, 11));
        });
        assertThat(targets).anySatisfy(t -> {
            assertThat(t.indexInfoId()).isEqualTo(kosdaq.getId());
            assertThat(t.startDate()).isEqualTo(LocalDate.of(2026, 3, 13));
        });
    }

    @Test
    @DisplayName("자동 연동 대상 생성 - 성공 이력과 실패 이력이 혼재하면 성공 이력 기준으로 startDate 계산")
    void buildAutoSyncTargets_mixedResults_usesSuccessDate() {
        IndexInfo indexInfo = indexInfoRepository.save(createIndexInfo("KOSPI"));
        saveSuccessTask(indexInfo, LocalDate.of(2026, 3, 10));
        integrationTaskRepository.save(IntegrationTask.create(
            indexInfo, JobType.INDEX_DATA.name(), LocalDate.of(2026, 3, 15),
            "SYSTEM", Instant.now(), "FAILURE", "에러 발생"
        ));

        List<AutoSyncTarget> targets = integrationTaskService.buildAutoSyncTargets(List.of(indexInfo.getId()));

        assertThat(targets.get(0).startDate()).isEqualTo(LocalDate.of(2026, 3, 11));
    }

    @Test
    @DisplayName("자동 연동 대상 생성 - 빈 목록이면 빈 결과 반환")
    void buildAutoSyncTargets_emptyInput_returnsEmptyList() {
        List<AutoSyncTarget> targets = integrationTaskService.buildAutoSyncTargets(List.of());

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
