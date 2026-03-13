package com.sprint.findex.service;

import com.sprint.findex.dto.autointegration.AutoIntegrationResponse;
import com.sprint.findex.entity.AutoIntegration;
import com.sprint.findex.entity.IndexInfo;
import com.sprint.findex.mapper.AutoIntegrationMapper;
import com.sprint.findex.repository.AutoIntegrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AutoIntegrationService {

    private final AutoIntegrationRepository autoIntegrationRepository;
    private final AutoIntegrationMapper autoIntegrationMapper;

    @Transactional
    public AutoIntegrationResponse createAutoIntegration(IndexInfo indexInfo) {
        AutoIntegration autoIntegration = AutoIntegration.create(indexInfo);
        AutoIntegration saved = autoIntegrationRepository.save(autoIntegration);

        return autoIntegrationMapper.toResponse(saved);
    }
}
