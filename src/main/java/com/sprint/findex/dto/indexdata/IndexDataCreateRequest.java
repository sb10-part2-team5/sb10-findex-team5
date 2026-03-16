package com.sprint.findex.dto.indexdata;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "지수 데이터 생성 요청")
public record IndexDataCreateRequest(
        @Schema(description = "지수 정보 ID", example = "9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d")
        @NotNull(message = "지수 정보 ID는 필수입니다.")
        UUID indexInfoId,

        @Schema(description = "기준 일자", example = "2023-01-01")
        @NotNull(message = "기준 일자는 필수입니다.")
        LocalDate baseDate,

        @Schema(description = "시가", example = "2800.25")
        @NotNull(message = "시가는 필수입니다.")
        @Positive(message = "시가는 양수여야 합니다.")
        BigDecimal marketPrice,

        @Schema(description = "종가", example = "2900.25")
        @NotNull(message = "종가는 필수입니다.")
        @Positive(message = "종가 양수여야 합니다.")
        BigDecimal closingPrice,

        @Schema(description = "고가", example = "3000.25")
        @NotNull(message = "고가는 필수입니다.")
        @Positive(message = "고가는 양수여야 합니다.")
        BigDecimal highPrice,

        @Schema(description = "저가", example = "2700.25")
        @NotNull(message = "저가는 필수입니다.")
        @Positive(message = "저가는 양수여야 합니다.")
        BigDecimal lowPrice,

        @Schema(description = "전일 대비 등락", example = "100.25")
        @NotNull(message = "전일 대비 등락값은 필수입니다.")
        BigDecimal versus,

        @Schema(description = "전일 대비 등락률", example = "5.5")
        @NotNull(message = "전일 대비 등락률은 필수입니다.")
        BigDecimal fluctuationRate,

        @Schema(description = "거래량", example = "1000000")
        @NotNull(message = "거래량은 필수입니다.")
        @PositiveOrZero(message = "거래량은 0 이상이어야 합니다.")
        Long tradingQuantity,

        @Schema(description = "거래대금", example = "2000000000")
        @NotNull(message = "거래대금은 필수입니다.")
        @PositiveOrZero(message = "거래대금은 0 이상이어야 합니다.")
        Long tradingPrice,

        @Schema(description = "시가총액", example = "3000000000000")
        @NotNull(message = "시가총액은 필수입니다.")
        @Positive(message = "시가총액은 양수여야 합니다.")
        Long marketTotalAmount
) {

}
