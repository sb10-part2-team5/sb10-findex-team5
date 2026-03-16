package com.sprint.findex.dto.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "순위가 포함된 지수 성과 정보 DTO")
public record RankedIndexPerformanceDto(
        @Schema(description = "지수 성과 정보")
        IndexPerformanceDto performance,

        @Schema(description = "순위", example = "1")
        int rank
) {
}
