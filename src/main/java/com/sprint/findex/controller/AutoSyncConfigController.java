package com.sprint.findex.controller;

import com.sprint.findex.dto.autosyncconfig.AutoSyncConfigDto;
import com.sprint.findex.dto.autosyncconfig.AutoSyncConfigQueryCondition;
import com.sprint.findex.dto.autosyncconfig.AutoSyncConfigUpdateRequest;
import com.sprint.findex.dto.response.PageResponse;
import com.sprint.findex.service.AutoSyncConfigService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auto-sync-configs")
public class AutoSyncConfigController {

    private final AutoSyncConfigService autoSyncConfigService;

    @PatchMapping("/{id}")
    public ResponseEntity<AutoSyncConfigDto> updateAutoSyncConfig(
            @PathVariable UUID id,
            @Valid @RequestBody AutoSyncConfigUpdateRequest request
    ) {
        AutoSyncConfigDto autoSyncConfigDto = autoSyncConfigService.updateAutoSyncConfig(id,
                request);

        return ResponseEntity.ok(autoSyncConfigDto);
    }

    @GetMapping
    public ResponseEntity<PageResponse<AutoSyncConfigDto>> findAllAutoSyncConfigs(
            @ParameterObject @Valid @ModelAttribute AutoSyncConfigQueryCondition condition
    ) {
        PageResponse<AutoSyncConfigDto> response = autoSyncConfigService.findAllAutoSyncConfigs(
                condition);

        return ResponseEntity.ok(response);
    }
}
