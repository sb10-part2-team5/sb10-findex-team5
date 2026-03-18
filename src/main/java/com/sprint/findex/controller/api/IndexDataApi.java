package com.sprint.findex.controller.api;

import com.sprint.findex.dto.dashboard.IndexChartDto;
import com.sprint.findex.dto.indexdata.IndexDataCreateRequest;
import com.sprint.findex.dto.indexdata.IndexDataDto;
import com.sprint.findex.dto.indexdata.IndexDataExportRequest;
import com.sprint.findex.dto.indexdata.IndexDataQueryCondition;
import com.sprint.findex.dto.indexdata.IndexDataUpdateRequest;
import com.sprint.findex.dto.response.PageResponse;
import com.sprint.findex.enums.ChartPeriodType;
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
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "지수 데이터 API")
public interface IndexDataApi {

    @Operation(summary = "지수 데이터 등록", description = "새로운 지수 데이터를 등록합니다.", operationId = "createIndexData")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "지수 데이터 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효하지 않은 데이터 값 등)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "참조하는 지수 정보를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<IndexDataDto> createIndexData(
            @RequestBody @Valid IndexDataCreateRequest request);

    @Operation(summary = "지수 데이터 수정", description = "기존 지수 데이터를 수정합니다.", operationId = "updateIndexData")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "지수 데이터 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효하지 않은 데이터 값 등)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "수정할 지수 데이터를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @Parameter(name = "id", description = "지수 데이터 ID")
    ResponseEntity<IndexDataDto> updateIndexData(
            @PathVariable UUID id,
            @RequestBody @Valid IndexDataUpdateRequest request
    );

    @Operation(summary = "지수 데이터 삭제", description = "지수 데이터를 삭제합니다.", operationId = "deleteIndexData")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "지수 데이터 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "삭제할 지수 데이터를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @Parameter(name = "id", description = "지수 정보 ID")
    ResponseEntity<Void> deleteIndexData(@PathVariable UUID id);

    @Operation(summary = "지수 데이터 다운로드", description = "지수 데이터를 CSV파일로 export합니다.", operationId = "exportIndexData")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "CSV 파일 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효하지 않은 필터 값 등)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<Resource> exportIndexData(
            @ParameterObject @Valid IndexDataExportRequest request);

    @Operation(summary = "지수 데이터 목록 조회", description = "지수 데이터 목록을 조회합니다. 필터링, 정렬, 커서 기반 페이지네이션을 지원합니다.", operationId = "getIndexDataList")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "지수 데이터 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효하지 않은 필터 값 등)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<PageResponse<IndexDataDto>> getIndexDataList(
            @Valid @ParameterObject IndexDataQueryCondition condition);

    @Operation(summary = "지수 차트 조회", description = "지수의 차트 데이터를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "차트 데이터 조회 성공",
                    content = @Content(schema = @Schema(implementation = IndexChartDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효하지 않은 기간 유형 등)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "지수 정보를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<IndexChartDto> getIndexChart(
            @Parameter(description = "지수 정보 ID", required = true) @PathVariable UUID id,
            @Parameter(description = "차트 기간 유형 (MONTHLY, QUARTERLY, YEARLY)") @RequestParam(defaultValue = "MONTHLY") ChartPeriodType periodType
    );
}