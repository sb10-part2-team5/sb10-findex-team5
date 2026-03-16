package com.sprint.findex.dto.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "지수 성과 정보 DTO")
public record IndexPerformanceDto(
        @Schema(description = "지수 정보ID", example = "1")
        UUID indexInfoId,

        @Schema(description = "지수 분류명", example = "KOSPI시리즈")
        String indexClassification,

        @Schema(description = "지수명", example = "IT 서비스")
        String indexName,

        @Schema(description = "단위 기간 대비 등락", example = "50.5")
        BigDecimal versus,

        @Schema(description = "단위 기간 대비 등락률", example = "1.8")
        BigDecimal fluctuationRate,

        @Schema(description = "현재가", example = "2850.75")
        BigDecimal currentPrice,

        @Schema(description = "단위 기간 전 값", example = "2850.75")
        BigDecimal beforePrice) {
}
