package com.sprint.findex.dto.autosyncconfig;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "자동 연동 설정 수정 요청")
public record AutoSyncConfigUpdateRequest(
        @Schema(description = "자동 연동 활성화 여부", example = "true")
        @NotNull
        Boolean enabled
) {

}
