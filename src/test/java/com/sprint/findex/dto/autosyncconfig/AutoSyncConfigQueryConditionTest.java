package com.sprint.findex.dto.autosyncconfig;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AutoSyncConfigQueryConditionTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    @DisplayName("기본값 적용 - null 입력 시")
    void defaultValues_appliedWhenNull() {
        AutoSyncConfigQueryCondition condition = new AutoSyncConfigQueryCondition(null, null, null, null, null, null, null);

        assertThat(condition.size()).isEqualTo(10);
        assertThat(condition.sortField()).isEqualTo("indexInfo.indexName");
        assertThat(condition.sortDirection()).isEqualTo("asc");
    }

    @Test
    @DisplayName("sortDirection 소문자 변환")
    void sortDirection_normalizedToLowerCase() {
        AutoSyncConfigQueryCondition condition = new AutoSyncConfigQueryCondition(null, null, null, null, null, "ASC", null);

        assertThat(condition.sortDirection()).isEqualTo("asc");
    }

    @Test
    @DisplayName("커서 쌍 유효 - 둘 다 null")
    void cursorPair_bothNull_valid() {
        AutoSyncConfigQueryCondition condition = new AutoSyncConfigQueryCondition(null, null, null, null, null, null, null);

        Set<ConstraintViolation<AutoSyncConfigQueryCondition>> violations = validator.validate(condition);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("커서 쌍 유효 - 둘 다 존재")
    void cursorPair_bothProvided_valid() {
        AutoSyncConfigQueryCondition condition = new AutoSyncConfigQueryCondition(null, null, UUID.randomUUID(), "IT 서비스", null, null, null);

        Set<ConstraintViolation<AutoSyncConfigQueryCondition>> violations = validator.validate(condition);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("커서 쌍 무효 - cursor만 존재")
    void cursorPair_onlyCursor_invalid() {
        AutoSyncConfigQueryCondition condition = new AutoSyncConfigQueryCondition(null, null, null, "IT 서비스", null, null, null);

        Set<ConstraintViolation<AutoSyncConfigQueryCondition>> violations = validator.validate(condition);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("cursorPairValid"));
    }

    @Test
    @DisplayName("커서 쌍 무효 - idAfter만 존재")
    void cursorPair_onlyIdAfter_invalid() {
        AutoSyncConfigQueryCondition condition = new AutoSyncConfigQueryCondition(null, null, UUID.randomUUID(), null, null, null, null);

        Set<ConstraintViolation<AutoSyncConfigQueryCondition>> violations = validator.validate(condition);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("cursorPairValid"));
    }

    @Test
    @DisplayName("sortField 패턴 무효")
    void sortField_invalidValue_invalid() {
        AutoSyncConfigQueryCondition condition = new AutoSyncConfigQueryCondition(null, null, null, null, "invalidField", null, null);

        Set<ConstraintViolation<AutoSyncConfigQueryCondition>> violations = validator.validate(condition);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("sortField"));
    }

    @Test
    @DisplayName("sortField 유효 - indexInfo.indexName")
    void sortField_indexName_valid() {
        AutoSyncConfigQueryCondition condition = new AutoSyncConfigQueryCondition(null, null, null, null, "indexInfo.indexName", null, null);

        Set<ConstraintViolation<AutoSyncConfigQueryCondition>> violations = validator.validate(condition);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("sortField 유효 - enabled")
    void sortField_enabled_valid() {
        AutoSyncConfigQueryCondition condition = new AutoSyncConfigQueryCondition(null, null, null, null, "enabled", null, null);

        Set<ConstraintViolation<AutoSyncConfigQueryCondition>> violations = validator.validate(condition);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("size 최솟값 위반")
    void size_zero_invalid() {
        AutoSyncConfigQueryCondition condition = new AutoSyncConfigQueryCondition(null, null, null, null, null, null, 0);

        Set<ConstraintViolation<AutoSyncConfigQueryCondition>> violations = validator.validate(condition);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("size"));
    }

    @Test
    @DisplayName("size 최댓값 위반")
    void size_overMax_invalid() {
        AutoSyncConfigQueryCondition condition = new AutoSyncConfigQueryCondition(null, null, null, null, null, null, 101);

        Set<ConstraintViolation<AutoSyncConfigQueryCondition>> violations = validator.validate(condition);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("size"));
    }

    @Test
    @DisplayName("size 경계값 유효 - 1, 100")
    void size_boundary_valid() {
        AutoSyncConfigQueryCondition conditionMin = new AutoSyncConfigQueryCondition(null, null, null, null, null, null, 1);
        AutoSyncConfigQueryCondition conditionMax = new AutoSyncConfigQueryCondition(null, null, null, null, null, null, 100);

        assertThat(validator.validate(conditionMin)).isEmpty();
        assertThat(validator.validate(conditionMax)).isEmpty();
    }
}
