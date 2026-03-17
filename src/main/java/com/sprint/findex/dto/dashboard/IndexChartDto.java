package com.sprint.findex.dto.dashboard;

import com.sprint.findex.enums.ChartPeriodType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Schema(description = "지수 차트 데이터 DTO")
public record IndexChartDto(
        @Schema(description = "지수 정보 ID", example = "1")
        UUID indexInfoId,

        @Schema(description = "지수 분류명", example = "KOSPI시리즈")
        String indexClassification,

        @Schema(description = "지수명", example = "IT 서비스")
        String indexName,

        @Schema(description = "차트 기간 유형 (MONTHLY, QUARTERLY, YEARLY)", example = "MONTHLY")
        ChartPeriodType periodType,

        @Schema(description = "차트 데이터 목록")
        List<ChartDataPoint> dataPoints,

        @Schema(description = "5일 이동평균선 데이터 목록")
        List<ChartDataPoint> ma5DataPoints,

        @Schema(description = "20일 이동평균선 데이터 목록")
        List<ChartDataPoint> ma20DataPoints
) {
    @Schema(description = "차트 데이터 포인트")
    public record ChartDataPoint(
            @Schema(description = "날짜", example = "2023-01-01")
            LocalDate date,

            @Schema(description = "값", example = "2850.75")
            BigDecimal value
    ) {
    }
}
