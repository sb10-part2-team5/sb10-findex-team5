package com.sprint.findex.controller;

import com.sprint.findex.dto.indexdata.IndexDataCreateRequest;
import com.sprint.findex.dto.indexdata.IndexDataDto;
import com.sprint.findex.dto.indexdata.IndexDataUpdateRequest;
import com.sprint.findex.service.IndexDataService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@RestController
@RequestMapping("/api/index-data")
@RequiredArgsConstructor
@Tag(name = "지수 데이터 API")
public class IndexDataController {

    private final IndexDataService indexDataService;

    @PostMapping
    public ResponseEntity<IndexDataDto> createIndexData(
            @RequestBody @Valid IndexDataCreateRequest request) {
        IndexDataDto response = indexDataService.save(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<IndexDataDto> updateIndexData(
            @PathVariable UUID id,
            @RequestBody @Valid IndexDataUpdateRequest request) {
        IndexDataDto response = indexDataService.update(id, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIndexData(@PathVariable UUID id) {
        indexDataService.delete(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping("/export/csv")
    public ResponseEntity<Resource> exportIndexData(
            @RequestParam(required = false) UUID indexInfoId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(defaultValue = "baseDate") String sortField,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        Resource response = indexDataService.export(indexInfoId, startDate, endDate, sortField,
                sortDirection);

        LocalDate now = LocalDate.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = now.format(formatter);

        String contentDisposition = ContentDisposition.attachment()
                .filename("index-data-" + formattedDate + ".csv", StandardCharsets.UTF_8)
                .build()
                .toString();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(response);

    }
}
