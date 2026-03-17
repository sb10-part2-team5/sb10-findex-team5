package com.sprint.findex.service;

import com.sprint.findex.dto.autosyncconfig.AutoSyncConfigDto;
import com.sprint.findex.dto.autosyncconfig.AutoSyncConfigQueryCondition;
import com.sprint.findex.dto.autosyncconfig.AutoSyncConfigUpdateRequest;
import com.sprint.findex.dto.response.PageResponse;
import com.sprint.findex.entity.AutoSyncConfig;
import com.sprint.findex.entity.IndexInfo;
import com.sprint.findex.exception.BusinessLogicException;
import com.sprint.findex.exception.ExceptionCode;
import com.sprint.findex.mapper.AutoSyncConfigMapper;
import com.sprint.findex.repository.AutoSyncConfigRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AutoSyncConfigService {

    private final AutoSyncConfigRepository autoSyncConfigRepository;
    private final AutoSyncConfigMapper autoSyncConfigMapper;

    @Transactional
    public AutoSyncConfigDto createAutoSyncConfig(
            IndexInfo indexInfo
    ) {
        AutoSyncConfig autoSyncConfig = AutoSyncConfig.create(indexInfo);
        AutoSyncConfig saved = autoSyncConfigRepository.save(autoSyncConfig);

        return autoSyncConfigMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public PageResponse<AutoSyncConfigDto> findAllAutoSyncConfigs(
            AutoSyncConfigQueryCondition condition
    ) {
        return autoSyncConfigRepository.findAllWithCondition(condition);
    }

    @Transactional
    public AutoSyncConfigDto updateAutoSyncConfig(
            UUID autoSyncConfigId,
            AutoSyncConfigUpdateRequest request
    ) {
        AutoSyncConfig autoSyncConfig = getAutoSyncConfigOrThrow(autoSyncConfigId);
        autoSyncConfig.updateEnabled(request.enabled());

        return autoSyncConfigMapper.toDto(autoSyncConfig);
    }

    @Transactional(readOnly = true)
    public List<UUID> findEnabledIndexInfoIds() {
        return autoSyncConfigRepository.findAllByEnabledTrue().stream()
                .map(config -> config.getIndexInfo().getId())
                .toList();
    }

    private AutoSyncConfig getAutoSyncConfigOrThrow(UUID autoSyncConfigId) {
        return autoSyncConfigRepository.findById(autoSyncConfigId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.AUTO_SYNC_CONFIG_NOT_FOUND));
    }
}
