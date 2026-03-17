package com.sprint.findex.controller;

import com.sprint.findex.dto.indexinfo.IndexInfoCreateRequest;
import com.sprint.findex.dto.indexinfo.IndexInfoDto;
import com.sprint.findex.dto.indexinfo.IndexInfoQueryCondition;
import com.sprint.findex.dto.indexinfo.IndexInfoSummaryDto;
import com.sprint.findex.dto.indexinfo.IndexInfoUpdateRequest;
import com.sprint.findex.dto.response.PageResponse;
import com.sprint.findex.exception.ErrorResponse;
import com.sprint.findex.service.IndexInfoService;
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
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/index-infos")
@RequiredArgsConstructor
@Tag(name = "지수 정보 API")
public class IndexInfoController {

  private final IndexInfoService indexInfoService;

  @Operation(summary = "지수 정보 등록", description = "새로운 지수 정보를 등록합니다.", operationId = "createIndexInfo")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "지수 정보 생성 성공"),
      @ApiResponse(responseCode = "400", description = "잘못된 요청 (필수 필드 누락 등)",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "500", description = "서버 오류",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PostMapping
  public ResponseEntity<IndexInfoDto> createIndexInfo(
      @Valid @RequestBody IndexInfoCreateRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(indexInfoService.createIndexInfoByUser(request));
  }

  @Operation(summary = "지수 정보 수정", description = "기존 지수 정보를 수정합니다.", operationId = "updateIndexInfo")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "지수 정보 수정 성공"),
      @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효하지 않은 필드 값 등)",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "수정할 지수 정보를 찾을 수 없음",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "500", description = "서버 오류",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @Parameter(name = "id", description = "지수 정보 ID")
  @PatchMapping("/{id}")
  public ResponseEntity<IndexInfoDto> updateIndexInfo(
      @Valid @RequestBody IndexInfoUpdateRequest request,
      @PathVariable UUID id) {
    return ResponseEntity.ok(indexInfoService.updateIndexInfoByUser(id, request));
  }

  @Operation(summary = "지수 정보 삭제", description = "지수 정보를 삭제합니다.관련된 지수 데이터도 함께 삭제됩니다.", operationId = "deleteIndexInfo")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "지수 정보 삭제 성공"),
      @ApiResponse(responseCode = "404", description = "삭제할 지수 정보를 찾을 수 없음",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "500", description = "서버 오류",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @Parameter(name = "id", description = "지수 정보 ID")
  @DeleteMapping("/{id}")
  public ResponseEntity<IndexInfoDto> deleteIndexInfo(
      @PathVariable UUID id) {
    indexInfoService.deleteIndexInfo(id);
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "지수 정보 목록 조회", description = "지수 정보 목록을 조회합니다. 필터링, 정렬, 커서 기반 페이지네이션을 지원합니다.", operationId = "getIndexInfoList")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "지수 정보 목록 조회 성공"),
      @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효하지 않은 필터 값 등)",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "500", description = "서버 오류",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @GetMapping
  public ResponseEntity<PageResponse<IndexInfoDto>> getIndexInfoList(
      @ParameterObject @ModelAttribute
      @Valid IndexInfoQueryCondition condition) {
    return ResponseEntity.ok(indexInfoService.getIndexInfoList(condition));
  }

  @Operation(summary = "지수 정보 요약 목록 조회", description = "지수 ID, 분류, 이름만 포함한 전체 지수 목록을 조회합니다.", operationId = "getIndexInfoSummaries")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "지수 정보 요약 목록 조회 성공"),
      @ApiResponse(responseCode = "500", description = "서버 오류",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @GetMapping("/summaries")
  public ResponseEntity<List<IndexInfoSummaryDto>> getIndexInfoSummaries() {
    return ResponseEntity.ok(indexInfoService.getSummaries());
  }
}
