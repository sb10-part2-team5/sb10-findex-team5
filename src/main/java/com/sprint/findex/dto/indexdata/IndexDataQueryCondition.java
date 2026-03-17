package com.sprint.findex.dto.indexdata;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.UUID;

public record IndexDataQueryCondition(
        @Schema(description = "지수 정보 ID")
        UUID indexInfoId,

        @Schema(description = "시작 날짜")
        LocalDate startDate,

        @Schema(description = "종료 날짜")
        LocalDate endDate,

        @Schema(description = "커서 값")
        String cursor,

        @Schema(description = "이전 페이지 마지막 요소 ID")
        UUID idAfter,

        @Schema(description = "페이지 크기", defaultValue = "10")
        @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.")
        Integer size,

        @Schema(description = "정렬 필드", defaultValue = "baseDate")
        IndexDataSortField sortField,

        @Schema(description = "정렬 방향", defaultValue = "desc", allowableValues = {"asc", "desc"})
        @Pattern(regexp = "^(?i)(asc|desc)$", message = "정렬 방향은 asc 또는 desc여야 합니다.")
        String sortDirection
) {

    public IndexDataQueryCondition {
        size = (size == null || size <= 0) ? 10 : size;
        sortField = (sortField == null) ? IndexDataSortField.baseDate : sortField;
        sortDirection = (sortDirection == null || sortDirection.isBlank()) ? "desc"
                : sortDirection.toLowerCase();
    }

    @AssertTrue(message = "시작 날짜는 종료 날짜보다 이전이어야 합니다.")
    public boolean isValidDateRange() {
        if (startDate == null || endDate == null) {
            return true;
        }
        return !startDate.isAfter(endDate);
    }
}