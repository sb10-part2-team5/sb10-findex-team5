package com.sprint.findex.dto.sync;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "자동 연동 대상 정보")
public record AutoSyncTarget(
    @Schema(description = "연동 대상 지수 정보 ID")
    UUID indexInfoId,

    @Schema(description = "연동 시작 날짜", example = "2026-03-01")
    LocalDate startDate
) {

}
