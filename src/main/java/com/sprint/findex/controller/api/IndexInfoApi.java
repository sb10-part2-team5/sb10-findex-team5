package com.sprint.findex.controller.api;

import com.sprint.findex.dto.indexinfo.IndexInfoCreateRequest;
import com.sprint.findex.dto.indexinfo.IndexInfoDto;
import com.sprint.findex.dto.indexinfo.IndexInfoQueryCondition;
import com.sprint.findex.dto.indexinfo.IndexInfoSummaryDto;
import com.sprint.findex.dto.indexinfo.IndexInfoUpdateRequest;
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
import java.util.List;
import java.util.UUID;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "지수 정보 API")
public interface IndexInfoApi {

    @Operation(summary = "지수 정보 등록", description = "새로운 지수 정보를 등록합니다.", operationId = "createIndexInfo")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "지수 정보 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (필수 필드 누락 등)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<IndexInfoDto> createIndexInfo(
            @Valid @RequestBody IndexInfoCreateRequest request);

    @Operation(summary = "지수 정보 수정", description = "기존 지수 정보를 수정합니다.", operationId = "updateIndexInfo")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "지수 정보 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효하지 않은 필드 값 등)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "수정할 지수 정보를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @Parameter(name = "id", description = "지수 정보 ID")
    ResponseEntity<IndexInfoDto> updateIndexInfo(
            @Valid @RequestBody IndexInfoUpdateRequest request,
            @PathVariable UUID id
    );

    @Operation(summary = "지수 정보 삭제", description = "지수 정보를 삭제합니다.관련된 지수 데이터도 함께 삭제됩니다.", operationId = "deleteIndexInfo")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "지수 정보 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "삭제할 지수 정보를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @Parameter(name = "id", description = "지수 정보 ID")
    ResponseEntity<IndexInfoDto> deleteIndexInfo(@PathVariable UUID id);

    @Operation(summary = "지수 정보 목록 조회", description = "지수 정보 목록을 조회합니다. 필터링, 정렬, 커서 기반 페이지네이션을 지원합니다.", operationId = "getIndexInfoList")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "지수 정보 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효하지 않은 필터 값 등)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<PageResponse<IndexInfoDto>> getIndexInfoList(
            @ParameterObject @ModelAttribute @Valid IndexInfoQueryCondition condition
    );

    @Operation(summary = "지수 정보 요약 목록 조회", description = "지수 ID, 분류, 이름만 포함한 전체 지수 목록을 조회합니다.", operationId = "getIndexInfoSummaries")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "지수 정보 요약 목록 조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<List<IndexInfoSummaryDto>> getIndexInfoSummaries();
}