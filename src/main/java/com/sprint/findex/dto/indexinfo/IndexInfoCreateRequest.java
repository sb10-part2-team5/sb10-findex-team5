package com.sprint.findex.dto.indexinfo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "지수 정보 생성 요청")
public record IndexInfoCreateRequest(
    @Schema(description = "지수 분류명", example = "KOSPI시리즈")
    @NotBlank
    String indexClassification,

    @Schema(description = "지수명", example = "IT 서비스")
    @NotBlank
    String indexName,

    @Schema(description = "채용 종목 수", example = "200")
    @NotNull
    @Min(0)
    Integer employedItemsCount,

    @Schema(description = "기준 시점", example = "2025-01-10")
    @NotNull
    LocalDate basePointInTime,

    @Schema(description = "기준 지수", example = "1000")
    @NotNull
    @Positive
    BigDecimal baseIndex,

    @Schema(description = "즐겨찾기 여부", example = "false")
    Boolean favorite
) {

}
