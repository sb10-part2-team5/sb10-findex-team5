package com.sprint.findex.dto.indexdata;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.UUID;

public record IndexDataExportRequest(
        @Schema(description = "지수 정보 ID")
        UUID indexInfoId,

        @Schema(description = "조회 시작일")
        LocalDate startDate,

        @Schema(description = "조회 종료일")
        LocalDate endDate,

        @Schema(description = "정렬 필드", defaultValue = "baseDate",
                allowableValues = {"baseDate", "marketPrice", "closingPrice", "highPrice",
                        "lowPrice", "versus", "fluctuationRate", "tradingQuantity"})
        String sortField,

        @Schema(description = "정렬 방향", defaultValue = "desc", allowableValues = {"asc", "desc"})
        String sortDirection
) {

    public IndexDataExportRequest {
        sortField = (sortField == null) ? "baseDate" : sortField;
        sortDirection = (sortDirection == null) ? "desc" : sortDirection;
    }
}
