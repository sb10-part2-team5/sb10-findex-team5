package com.sprint.findex.dto.autosyncconfig;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import java.util.UUID;

@Schema(description = "커서 기반 자동 연동 설정 목록 조회 파라미터")
public record AutoSyncConfigQueryCondition(
        @Schema(description = "지수 정보 ID",
                example = "123e4567-e89b-12d3-a456-426614174001")
        UUID indexInfoId,

        @Schema(description = "활성화 여부", example = "true")
        Boolean enabled,

        @Schema(description = "이전 페이지 마지막 요소 ID",
                example = "123e4567-e89b-12d3-a456-426614174000")
        UUID idAfter,

        @Schema(description = "커서 (다음 페이지 시작점)", example = "IT 서비스")
        String cursor,

        @Schema(description = "정렬 필드 (indexInfo.indexName, enabled)",
                defaultValue = "indexInfo.indexName")
        @Pattern(regexp = "indexInfo\\.indexName|enabled")
        String sortField,

        @Schema(description = "정렬 방향 (asc, desc)", defaultValue = "asc")
        @Pattern(regexp = "(?i)(asc|desc)")
        String sortDirection,

        @Schema(description = "페이지 크기", defaultValue = "10")
        @Min(1)
        @Max(100)
        Integer size
) {
    public AutoSyncConfigQueryCondition {
        if (size == null) {
            size = 10;
        }
        if (sortField == null || sortField.isBlank()) {
            sortField = "indexInfo.indexName";
        }
        if (sortDirection == null || sortDirection.isBlank()) {
            sortDirection = "asc";
        } else {
            sortDirection = sortDirection.toLowerCase();
        }
    }

    @AssertTrue(message = "cursor와 idAfter는 함께 요청되어야 합니다.")
    @Schema(hidden = true)
    public boolean isCursorPairValid() {
        return (cursor == null && idAfter == null) || (cursor != null && idAfter != null);
    }
}
