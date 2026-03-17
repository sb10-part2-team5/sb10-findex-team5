package com.sprint.findex.dto.sync;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Schema(description = "지수 데이터 연동 요청")
public record IndexDataSyncRequest(
        @Schema(description = "지수 정보 ID 목록 (비어있을 경우 모든 지수 대상)")
        List<UUID> indexInfoIds,

        @Schema(description = "대상 날짜 (부터)", example = "2023-01-01")
        @NotNull
        LocalDate baseDateFrom,

        @Schema(description = "대상 날짜 (까지)", example = "2023-01-31")
        @NotNull
        LocalDate baseDateTo
) {

}
