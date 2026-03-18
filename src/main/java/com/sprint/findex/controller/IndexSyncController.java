package com.sprint.findex.controller;

import com.sprint.findex.dto.response.PageResponse;
import com.sprint.findex.dto.sync.CursorPageResponseSyncJobDto;
import com.sprint.findex.dto.sync.IndexDataSyncRequest;
import com.sprint.findex.dto.sync.SyncJobDto;
import com.sprint.findex.dto.sync.SyncJobQueryCondition;
import com.sprint.findex.exception.ErrorResponse;
import com.sprint.findex.service.IndexSyncService;
import com.sprint.findex.service.IntegrationTaskService;
import com.sprint.findex.util.ClientIpResolver;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sync-jobs")
@Tag(name = "연동 작업 API", description = "연동 작업 관리 API")
public class IndexSyncController {

    private final IndexSyncService indexSyncService;
    private final IntegrationTaskService integrationTaskService;
    private final ClientIpResolver clientIpResolver;

    @Operation(summary = "지수 정보 연동", description = "Open API를 통해 지수 정보를 연동합니다.", operationId = "syncIndexInfo")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "연동 작업 생성 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = SyncJobDto.class)))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/index-infos")
    public ResponseEntity<List<SyncJobDto>> syncIndexInfo(HttpServletRequest request) {
        // 사용자 IP 추출
        String worker = clientIpResolver.resolve(request);
        List<SyncJobDto> syncJobDtos = indexSyncService.syncIndexInfo(worker);
        return ResponseEntity.accepted().body(syncJobDtos);
    }

    @Operation(summary = "지수 데이터 연동", description = "Open API를 통해 지수 데이터를 연동합니다.", operationId = "syncIndexData")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "연동 작업 생성 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = SyncJobDto.class)))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효하지 않은 날짜 범위 등)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "지수 정보를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/index-data")
    public ResponseEntity<List<SyncJobDto>> syncIndexData(
            @Valid @RequestBody IndexDataSyncRequest indexDataSyncRequest,
            HttpServletRequest request
    ) {
        String worker = clientIpResolver.resolve(request);
        List<SyncJobDto> syncJobDtos = indexSyncService.syncIndexData(indexDataSyncRequest, worker);
        return ResponseEntity.accepted().body(syncJobDtos);
    }

    @Operation(
            summary = "연동 작업 목록 조회", description = "연동 작업 목록을 조회합니다. 필터링, 정렬, 커서 기반 페이지네이션을 지원합니다.",
            operationId = "getSyncJobList"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "연동 작업 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = PageResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효하지 않은 필터 값 등)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<CursorPageResponseSyncJobDto> getSyncJobList(
            @ParameterObject @Valid @ModelAttribute SyncJobQueryCondition condition
    ) {
        return ResponseEntity.ok(integrationTaskService.getSyncJobList(condition));
    }
}
