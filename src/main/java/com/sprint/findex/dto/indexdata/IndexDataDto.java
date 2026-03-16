package com.sprint.findex.dto.indexdata;

import com.sprint.findex.enums.SourceType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "지수 데이터 DTO")
public record IndexDataDto(
        @Schema(description = "지수 데이터 ID", example = "9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d")
        UUID id,

        @Schema(description = "지수 정보 ID", example = "9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d")
        UUID indexInfoId,

        @Schema(description = "기준 일자", example = "2023-01-01")
        LocalDate baseDate,

        @Schema(description = "출처(사용자,Oen API)", allowableValues = "USER, OPEN_API", example = "USER")
        SourceType sourceType,

        @Schema(description = "시가", example = "2800.25")
        BigDecimal marketPrice,

        @Schema(description = "종가", example = "2900.25")
        BigDecimal closingPrice,

        @Schema(description = "고가", example = "3000.25")
        BigDecimal highPrice,

        @Schema(description = "저가", example = "2700.25")
        BigDecimal lowPrice,

        @Schema(description = "전일 대비 등락", example = "100.25")
        BigDecimal versus,

        @Schema(description = "전일 대비 등락률", example = "5.5")
        BigDecimal fluctuationRate,

        @Schema(description = "거래량", example = "1000000")
        Long tradingQuantity,

        @Schema(description = "거래대금", example = "2000000000")
        Long tradingPrice,

        @Schema(description = "시가총액", example = "3000000000000")
        Long marketTotalAmount
) {

}
