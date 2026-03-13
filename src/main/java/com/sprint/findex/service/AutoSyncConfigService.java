package com.sprint.findex.service;

import com.sprint.findex.dto.autosyncconfig.AutoSyncConfigResponse;
import com.sprint.findex.dto.autosyncconfig.AutoSyncConfigUpdateRequest;
import com.sprint.findex.entity.AutoSyncConfig;
import com.sprint.findex.entity.IndexInfo;
import com.sprint.findex.exception.BusinessLogicException;
import com.sprint.findex.exception.ExceptionCode;
import com.sprint.findex.mapper.AutoSyncConfigMapper;
import com.sprint.findex.repository.AutoSyncConfigRepository;
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
    public AutoSyncConfigResponse createAutoSyncConfig(
            IndexInfo indexInfo
    ) {
        AutoSyncConfig autoSyncConfig = AutoSyncConfig.create(indexInfo);
        AutoSyncConfig saved = autoSyncConfigRepository.save(autoSyncConfig);

        return autoSyncConfigMapper.toResponse(saved);
    }

    @Transactional
    public AutoSyncConfigResponse updateAutoSyncConfig(
            UUID autoSyncConfigId,
            AutoSyncConfigUpdateRequest request
    ) {
        AutoSyncConfig autoSyncConfig = getAutoSyncConfigOrThrow(autoSyncConfigId);
        autoSyncConfig.updateEnabled(request.enabled());

        return autoSyncConfigMapper.toResponse(autoSyncConfig);
    }

    private AutoSyncConfig getAutoSyncConfigOrThrow(UUID autoSyncConfigId) {
        return autoSyncConfigRepository.findById(autoSyncConfigId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.AUTO_SYNC_CONFIG_NOT_FOUND));
    }
}
