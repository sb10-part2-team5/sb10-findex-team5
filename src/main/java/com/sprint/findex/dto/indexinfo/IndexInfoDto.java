package com.sprint.findex.dto.indexinfo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
@Schema(description = "지수 정보 DTO")
public record IndexInfoDto(
    @Schema(description = "지수 정보 ID", example = "1")
    UUID id,

    @Schema(description = "지수 분류명", example = "KOSPI시리즈")
    String indexClassification,

    @Schema(description = "지수명", example = "IT 서비스")
    String indexName,

    @Schema(description = "채용 종목 수",example = "200")
    Integer employedItemsCount,

    @Schema(description = "기준 시점", example = "2026-01-02")
    LocalDate basePointInTime,

    @Schema(description = "기준 지수", example = "1000")
    BigDecimal baseIndex,

    @Schema(description = "출처 (사용자/OPEN API)",example = "OPEN_API")
    String sourceType,

    @Schema(description = "즐겨찾기 여부",example = "true")
    Boolean favorite
) {

}
