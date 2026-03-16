package com.sprint.findex.controller;

import com.sprint.findex.dto.dashboard.IndexPerformanceDto;
import com.sprint.findex.enums.PeriodType;
import com.sprint.findex.exception.ErrorResponse;
import com.sprint.findex.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/index-data/performance")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/favorite")
    @Operation(
            summary = "관심 지수 성과 조회",
            description = "즐겨찾기로 등록된 지수들의 성과를 조회합니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "관심 지수 성과 조회 성공",
            content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = IndexPerformanceDto.class))
            )
    )
    @ApiResponse(
            responseCode = "500",
            description = "서버 오류",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    public ResponseEntity<List<IndexPerformanceDto>> getIndexPerformance(
            @Parameter(description = "성과 기간 유형 (DAILY, WEEKLY, MONTHLY)")
            @RequestParam(defaultValue = "DAILY") PeriodType periodType
    ) {
        List<IndexPerformanceDto> response = dashboardService.getFavoriteIndexPerformance(periodType);
        return ResponseEntity.ok(response);
    }
}
