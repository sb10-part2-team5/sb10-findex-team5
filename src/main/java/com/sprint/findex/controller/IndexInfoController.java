package com.sprint.findex.controller;

import com.sprint.findex.dto.indexinfo.IndexInfoCreateRequest;
import com.sprint.findex.dto.indexinfo.IndexInfoDto;
import com.sprint.findex.exception.ErrorResponse;
import com.sprint.findex.service.IndexInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
}
