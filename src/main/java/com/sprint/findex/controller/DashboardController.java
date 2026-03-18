package com.sprint.findex.controller;

import com.sprint.findex.dto.dashboard.IndexPerformanceDto;
import com.sprint.findex.dto.dashboard.RankedIndexPerformanceDto;
import com.sprint.findex.enums.PeriodType;
import com.sprint.findex.service.DashboardService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/index-data/performance")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/favorite")
    public ResponseEntity<List<IndexPerformanceDto>> getIndexPerformance(
            @RequestParam(defaultValue = "DAILY") PeriodType periodType
    ) {
        List<IndexPerformanceDto> response = dashboardService.getFavoriteIndexPerformance(
                periodType);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/rank")
    public ResponseEntity<List<RankedIndexPerformanceDto>> getRankedIndexPerformance(
            @RequestParam(required = false) UUID indexInfoId,
            @RequestParam(defaultValue = "DAILY") PeriodType periodType,
            @RequestParam(defaultValue = "10") @Min(1) @Max(10) Integer limit
    ) {
        List<RankedIndexPerformanceDto> response =
                dashboardService.getRankedIndexPerformance(indexInfoId, periodType, limit);
        return ResponseEntity.ok(response);
    }
}
