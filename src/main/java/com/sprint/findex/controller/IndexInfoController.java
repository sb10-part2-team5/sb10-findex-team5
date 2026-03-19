package com.sprint.findex.controller;

import com.sprint.findex.dto.indexinfo.IndexInfoCreateRequest;
import com.sprint.findex.dto.indexinfo.IndexInfoDto;
import com.sprint.findex.dto.indexinfo.IndexInfoQueryCondition;
import com.sprint.findex.dto.indexinfo.IndexInfoSummaryDto;
import com.sprint.findex.dto.indexinfo.IndexInfoUpdateRequest;
import com.sprint.findex.dto.response.PageResponse;
import com.sprint.findex.service.IndexInfoService;
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
public class IndexInfoController {

    private final IndexInfoService indexInfoService;

    @PostMapping
    public ResponseEntity<IndexInfoDto> createIndexInfo(
            @Valid @RequestBody IndexInfoCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(indexInfoService.createIndexInfoByUser(request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<IndexInfoDto> updateIndexInfo(
            @Valid @RequestBody IndexInfoUpdateRequest request,
            @PathVariable UUID id) {
        return ResponseEntity.ok(indexInfoService.updateIndexInfoByUser(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteIndexInfo(
            @PathVariable UUID id) {
        indexInfoService.deleteIndexInfo(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<PageResponse<IndexInfoDto>> getIndexInfoList(
            @ParameterObject @ModelAttribute
            @Valid IndexInfoQueryCondition condition) {
        return ResponseEntity.ok(indexInfoService.getIndexInfoList(condition));
    }

    @GetMapping("/summaries")
    public ResponseEntity<List<IndexInfoSummaryDto>> getIndexInfoSummaries() {
        return ResponseEntity.ok(indexInfoService.getSummaries());
    }
}
