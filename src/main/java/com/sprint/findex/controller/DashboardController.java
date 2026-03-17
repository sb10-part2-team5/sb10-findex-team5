package com.sprint.findex.controller;

import com.sprint.findex.dto.dashboard.IndexChartDto;
import com.sprint.findex.dto.dashboard.IndexPerformanceDto;
import com.sprint.findex.dto.dashboard.RankedIndexPerformanceDto;
import com.sprint.findex.enums.ChartPeriodType;
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
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/index-data/performance")
@Tag(name = "지수 데이터 API")
public class DashboardController {

    private final DashboardService dashboardService;

    @Operation(
            summary = "주요 지수 현황 조회",
            description = "즐겨찾기된 지수의 성과 정보를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "주요 지수 현황 조회 성공",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = IndexPerformanceDto.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/favorite")
    public ResponseEntity<List<IndexPerformanceDto>> getIndexPerformance(
            @Parameter(description = "성과 기간 유형 (DAILY, WEEKLY, MONTHLY)")
            @RequestParam(defaultValue = "DAILY") PeriodType periodType
    ) {
        List<IndexPerformanceDto> response = dashboardService.getFavoriteIndexPerformance(periodType);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "지수 성과 랭킹 조회",
            description = "지수의 성과 분석 랭킹을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "성과 랭킹 조회 성공",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = RankedIndexPerformanceDto.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/rank")
    public ResponseEntity<List<RankedIndexPerformanceDto>> getRankedIndexPerformance(
            @Parameter(description = "지수 정보 ID", required = false)
            @RequestParam(required = false) UUID indexInfoId,
            @Parameter(description = "성과 기간 유형 (DAILY, WEEKLY, MONTHLY)")
            @RequestParam(defaultValue = "DAILY") PeriodType periodType,
            @Parameter(description = "최대 랭킹 수")
            @RequestParam(defaultValue = "10") @Min(1) @Max(10) Integer limit
    ) {
        List<RankedIndexPerformanceDto> response =
                dashboardService.getRankedIndexPerformance(indexInfoId, periodType, limit);
        return ResponseEntity.ok(response);
    }
}
