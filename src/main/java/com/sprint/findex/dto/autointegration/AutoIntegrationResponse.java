package com.sprint.findex.dto.autointegration;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "자동 연동 응답")
public record AutoIntegrationResponse(
    @Schema(description = "자동 연동 ID", example = "123e4567-e89b-12d3-a456-426614174000")
    @NotNull UUID id,

    @Schema(description = "지수 정보 ID", example = "123e4567-e89b-12d3-a456-426614174001")
    @NotNull UUID indexInfoId,

    @Schema(description = "지수 분류", example = "KOSPI시리즈")
    @NotBlank String indexClassification,

    @Schema(description = "지수명", example = "IT 서비스")
    @NotBlank String indexName,

    @Schema(description = "자동 연동 활성화 여부", example = "false")
    boolean enabled
) {}
