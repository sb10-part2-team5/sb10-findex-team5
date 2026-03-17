package com.sprint.findex.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sprint.findex.dto.autosyncconfig.AutoSyncConfigDto;
import com.sprint.findex.dto.autosyncconfig.AutoSyncConfigQueryCondition;
import com.sprint.findex.dto.autosyncconfig.AutoSyncConfigUpdateRequest;
import com.sprint.findex.dto.response.PageResponse;
import com.sprint.findex.entity.IndexInfo;
import com.sprint.findex.enums.SourceType;
import com.sprint.findex.exception.BusinessLogicException;
import com.sprint.findex.exception.ExceptionCode;
import com.sprint.findex.repository.AutoSyncConfigRepository;
import com.sprint.findex.repository.IndexInfoRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class AutoSyncConfigServiceTest {

    @Autowired
    private AutoSyncConfigService autoSyncConfigService;

    @Autowired
    private IndexInfoRepository indexInfoRepository;

    @Autowired
    private AutoSyncConfigRepository autoSyncConfigRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("자동 연동 설정 생성 성공")
    void createAutoSyncConfig_success() {
        IndexInfo savedIndexInfo = indexInfoRepository.save(createIndexInfo());

        AutoSyncConfigDto result = autoSyncConfigService.createAutoSyncConfig(savedIndexInfo);

        assertThat(result.id()).isNotNull();
        assertThat(result.indexInfoId()).isEqualTo(savedIndexInfo.getId());
        assertThat(result.indexClassification()).isEqualTo(savedIndexInfo.getIndexClassification());
        assertThat(result.indexName()).isEqualTo(savedIndexInfo.getIndexName());
        assertThat(result.enabled()).isFalse();
        assertThat(autoSyncConfigRepository.findById(result.id())).isPresent();
    }

    @Test
    @DisplayName("자동 연동 설정 수정 성공")
    void updateAutoSyncConfig_success() {
        IndexInfo savedIndexInfo = indexInfoRepository.save(createIndexInfo());
        AutoSyncConfigDto created = autoSyncConfigService.createAutoSyncConfig(savedIndexInfo);
        AutoSyncConfigUpdateRequest request = new AutoSyncConfigUpdateRequest(true);

        AutoSyncConfigDto result = autoSyncConfigService.updateAutoSyncConfig(created.id(), request);

        entityManager.flush();
        entityManager.clear();

        assertThat(result.id()).isEqualTo(created.id());
        assertThat(result.indexInfoId()).isEqualTo(savedIndexInfo.getId());
        assertThat(result.enabled()).isTrue();

        assertThat(autoSyncConfigRepository.findById(created.id()))
                .get()
                .extracting(autoSyncConfig -> autoSyncConfig.isEnabled())
                .isEqualTo(true);
    }

    @Test
    @DisplayName("자동 연동 설정 수정 실패 - 존재하지 않는 설정")
    void updateAutoSyncConfig_notFound() {
        AutoSyncConfigUpdateRequest request = new AutoSyncConfigUpdateRequest(true);

        assertThatThrownBy(() -> autoSyncConfigService.updateAutoSyncConfig(UUID.randomUUID(), request))
                .isInstanceOf(BusinessLogicException.class)
                .extracting(exception -> ((BusinessLogicException) exception).getExceptionCode())
                .isEqualTo(ExceptionCode.AUTO_SYNC_CONFIG_NOT_FOUND);
    }

    @Test
    @DisplayName("목록 조회 성공 - 기본 조건")
    void findAllAutoSyncConfigs_defaultCondition_success() {
        createAutoSyncConfigWith("가 서비스");
        createAutoSyncConfigWith("나 서비스");
        createAutoSyncConfigWith("다 서비스");

        AutoSyncConfigQueryCondition condition = new AutoSyncConfigQueryCondition(null, null, null, null, null, null, null);
        PageResponse<AutoSyncConfigDto> result = autoSyncConfigService.findAllAutoSyncConfigs(condition);

        assertThat(result.content()).hasSize(3);
        assertThat(result.totalElements()).isEqualTo(3L);
        assertThat(result.hasNext()).isFalse();
    }

    @Test
    @DisplayName("목록 조회 성공 - enabled 필터")
    void findAllAutoSyncConfigs_filterByEnabled_success() {
        AutoSyncConfigDto config1 = createAutoSyncConfigWith("가 서비스");
        createAutoSyncConfigWith("나 서비스");

        autoSyncConfigService.updateAutoSyncConfig(config1.id(), new AutoSyncConfigUpdateRequest(true));

        AutoSyncConfigQueryCondition condition = new AutoSyncConfigQueryCondition(null, true, null, null, null, null, null);
        PageResponse<AutoSyncConfigDto> result = autoSyncConfigService.findAllAutoSyncConfigs(condition);

        assertThat(result.content()).allMatch(AutoSyncConfigDto::enabled);
    }

    @Test
    @DisplayName("목록 조회 성공 - indexInfoId 필터")
    void findAllAutoSyncConfigs_filterByIndexInfoId_success() {
        AutoSyncConfigDto target = createAutoSyncConfigWith("가 서비스");
        createAutoSyncConfigWith("나 서비스");

        AutoSyncConfigQueryCondition condition = new AutoSyncConfigQueryCondition(target.indexInfoId(), null, null, null, null, null, null);
        PageResponse<AutoSyncConfigDto> result = autoSyncConfigService.findAllAutoSyncConfigs(condition);

        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).indexInfoId()).isEqualTo(target.indexInfoId());
    }

    @Test
    @DisplayName("목록 조회 성공 - indexName 오름차순")
    void findAllAutoSyncConfigs_sortByIndexName_asc_success() {
        createAutoSyncConfigWith("다 서비스");
        createAutoSyncConfigWith("가 서비스");
        createAutoSyncConfigWith("나 서비스");

        AutoSyncConfigQueryCondition condition = new AutoSyncConfigQueryCondition(null, null, null, null, "indexInfo.indexName", "asc", null);
        PageResponse<AutoSyncConfigDto> result = autoSyncConfigService.findAllAutoSyncConfigs(condition);

        List<String> names = result.content().stream().map(AutoSyncConfigDto::indexName).toList();
        assertThat(names).isSorted();
    }

    @Test
    @DisplayName("목록 조회 성공 - 다음 페이지 존재")
    void findAllAutoSyncConfigs_hasNext_success() {
        createAutoSyncConfigWith("가 서비스");
        createAutoSyncConfigWith("나 서비스");
        createAutoSyncConfigWith("다 서비스");

        AutoSyncConfigQueryCondition condition = new AutoSyncConfigQueryCondition(null, null, null, null, null, null, 2);
        PageResponse<AutoSyncConfigDto> result = autoSyncConfigService.findAllAutoSyncConfigs(condition);

        assertThat(result.hasNext()).isTrue();
        assertThat(result.nextCursor()).isNotNull();
        assertThat(result.nextIdAfter()).isNotNull();
    }

    @Test
    @DisplayName("목록 조회 성공 - enabled 필터 + indexName 정렬")
    void findAllAutoSyncConfigs_filterByEnabledAndSortByIndexName_success() {
        AutoSyncConfigDto naService = createAutoSyncConfigWith("나 서비스");
        AutoSyncConfigDto gaService = createAutoSyncConfigWith("가 서비스");
        createAutoSyncConfigWith("라 서비스");
        AutoSyncConfigDto daService = createAutoSyncConfigWith("다 서비스");

        autoSyncConfigService.updateAutoSyncConfig(gaService.id(), new AutoSyncConfigUpdateRequest(true));
        autoSyncConfigService.updateAutoSyncConfig(daService.id(), new AutoSyncConfigUpdateRequest(true));

        AutoSyncConfigQueryCondition condition = new AutoSyncConfigQueryCondition(null, true, null, null, "indexInfo.indexName", "asc", null);
        PageResponse<AutoSyncConfigDto> result = autoSyncConfigService.findAllAutoSyncConfigs(condition);

        assertThat(result.content()).hasSize(2);
        assertThat(result.content()).allMatch(AutoSyncConfigDto::enabled);
        List<String> names = result.content().stream().map(AutoSyncConfigDto::indexName).toList();
        assertThat(names).isSorted();
        assertThat(names).containsExactly("가 서비스", "다 서비스");
    }

    @Test
    @DisplayName("목록 조회 성공 - 커서 페이지네이션")
    void findAllAutoSyncConfigs_cursorPagination_success() {
        createAutoSyncConfigWith("가 서비스");
        createAutoSyncConfigWith("나 서비스");
        createAutoSyncConfigWith("다 서비스");

        AutoSyncConfigQueryCondition firstPage = new AutoSyncConfigQueryCondition(null, null, null, null, "indexInfo.indexName", "asc", 2);
        PageResponse<AutoSyncConfigDto> first = autoSyncConfigService.findAllAutoSyncConfigs(firstPage);

        assertThat(first.hasNext()).isTrue();

        AutoSyncConfigQueryCondition secondPage = new AutoSyncConfigQueryCondition(null, null, first.nextIdAfter(), first.nextCursor(), "indexInfo.indexName", "asc", 2);
        PageResponse<AutoSyncConfigDto> second = autoSyncConfigService.findAllAutoSyncConfigs(secondPage);

        assertThat(second.content()).hasSize(1);
        assertThat(second.content().get(0).indexName()).isEqualTo("다 서비스");
    }

    @Test
    @DisplayName("활성화된 지수 ID 목록 조회 - enabled=true인 지수만 반환")
    void findEnabledIndexInfoIds_returnsOnlyEnabledIds() {
        AutoSyncConfigDto enabled1 = createAutoSyncConfigWith("가 서비스");
        AutoSyncConfigDto enabled2 = createAutoSyncConfigWith("나 서비스");
        createAutoSyncConfigWith("다 서비스");

        autoSyncConfigService.updateAutoSyncConfig(enabled1.id(), new AutoSyncConfigUpdateRequest(true));
        autoSyncConfigService.updateAutoSyncConfig(enabled2.id(), new AutoSyncConfigUpdateRequest(true));

        List<UUID> result = autoSyncConfigService.findEnabledIndexInfoIds();

        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(enabled1.indexInfoId(), enabled2.indexInfoId());
    }

    @Test
    @DisplayName("활성화된 지수 ID 목록 조회 - enabled=true인 설정이 없으면 빈 목록 반환")
    void findEnabledIndexInfoIds_noneEnabled_returnsEmptyList() {
        createAutoSyncConfigWith("가 서비스");
        createAutoSyncConfigWith("나 서비스");

        List<UUID> result = autoSyncConfigService.findEnabledIndexInfoIds();

        assertThat(result).isEmpty();
    }

    private AutoSyncConfigDto createAutoSyncConfigWith(String indexName) {
        IndexInfo indexInfo = IndexInfo.create(
                indexName,
                "KOSPI시리즈",
                42,
                LocalDate.of(2024, 1, 2),
                new BigDecimal("1000.1234"),
                SourceType.USER,
                false
        );
        IndexInfo savedIndexInfo = indexInfoRepository.save(indexInfo);
        return autoSyncConfigService.createAutoSyncConfig(savedIndexInfo);
    }

    private IndexInfo createIndexInfo() {
        return IndexInfo.create(
                "IT 서비스",
                "KOSPI시리즈",
                42,
                LocalDate.of(2024, 1, 2),
                new BigDecimal("1000.1234"),
                SourceType.USER,
                false
        );
    }
}
