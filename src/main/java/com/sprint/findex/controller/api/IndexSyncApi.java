package com.sprint.findex.controller.api;

import com.sprint.findex.dto.response.PageResponse;
import com.sprint.findex.dto.sync.IndexDataSyncRequest;
import com.sprint.findex.dto.sync.SyncJobDto;
import com.sprint.findex.dto.sync.SyncJobQueryCondition;
import com.sprint.findex.exception.ErrorResponse;
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
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "연동 작업 API", description = "연동 작업 관리 API")
public interface IndexSyncApi {

    @Operation(summary = "지수 정보 연동", description = "Open API를 통해 지수 정보를 연동합니다.", operationId = "syncIndexInfo")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "연동 작업 생성 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = SyncJobDto.class)))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<List<SyncJobDto>> syncIndexInfo(HttpServletRequest request);

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
    ResponseEntity<List<SyncJobDto>> syncIndexData(
            @Valid @RequestBody IndexDataSyncRequest indexDataSyncRequest,
            HttpServletRequest request
    );

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
    ResponseEntity<PageResponse<SyncJobDto>> getSyncJobList(
            @ParameterObject @Valid @ModelAttribute SyncJobQueryCondition condition
    );
}