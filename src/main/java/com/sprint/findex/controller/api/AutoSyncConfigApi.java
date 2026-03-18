package com.sprint.findex.controller.api;

import com.sprint.findex.dto.autosyncconfig.AutoSyncConfigDto;
import com.sprint.findex.dto.autosyncconfig.AutoSyncConfigQueryCondition;
import com.sprint.findex.dto.autosyncconfig.AutoSyncConfigUpdateRequest;
import com.sprint.findex.dto.response.PageResponse;
import com.sprint.findex.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "자동 연동 설정 API", description = "자동 연동 설정 관리 API")
public interface AutoSyncConfigApi {

    @Operation(summary = "자동 연동 설정 수정", description = "기존 자동 연동 설정을 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "자동 연동 설정 수정 성공",
                    content = @Content(schema = @Schema(implementation = AutoSyncConfigDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효하지 않은 설정 값 등)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "수정할 자동 연동 설정을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    ResponseEntity<AutoSyncConfigDto> updateAutoSyncConfig(
            @Parameter(description = "자동 연동 설정 ID", example = "123e4567-e89b-12d3-a456-426614174000") @PathVariable UUID id,
            @Valid @RequestBody AutoSyncConfigUpdateRequest request
    );

    @Operation(
            summary = "자동 연동 설정 목록 조회",
            description = "자동 연동 설정 목록을 조회합니다. 지수, 활성화 여부 필터링과 정렬, 커서 기반 페이지네이션을 지원합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "자동 연동 설정 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = PageResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효하지 않은 필터 값 등)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    ResponseEntity<PageResponse<AutoSyncConfigDto>> findAllAutoSyncConfigs(
            @ParameterObject @Valid @ModelAttribute AutoSyncConfigQueryCondition condition
    );
}