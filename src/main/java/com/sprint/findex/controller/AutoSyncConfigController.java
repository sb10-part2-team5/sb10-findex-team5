package com.sprint.findex.controller;

import com.sprint.findex.dto.autosyncconfig.AutoSyncConfigResponse;
import com.sprint.findex.dto.autosyncconfig.AutoSyncConfigUpdateRequest;
import com.sprint.findex.service.AutoSyncConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auto-sync-configs")
@Tag(name = "자동 연동 설정 API", description = "자동 연동 설정 관리 API")
public class AutoSyncConfigController {

    private final AutoSyncConfigService autoSyncConfigService;

    @Operation(
            summary = "자동 연동 설정 수정",
            description = "기존 자동 연동 설정을 수정합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "자동 연동 설정 수정 성공",
                    content = @Content(schema = @Schema(implementation = AutoSyncConfigResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "자동 연동 설정을 찾을 수 없음")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<AutoSyncConfigResponse> updateAutoSyncConfig(
            @Parameter(description = "자동 연동 설정 ID", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id,
            @RequestBody AutoSyncConfigUpdateRequest request
    ) {
        AutoSyncConfigResponse autoSyncConfigResponse = autoSyncConfigService.updateAutoSyncConfig(id, request);

        return ResponseEntity.ok(autoSyncConfigResponse);
    }
}
