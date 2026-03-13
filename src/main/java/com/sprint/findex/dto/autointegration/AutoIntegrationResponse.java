package com.sprint.findex.dto.autointegration;

import java.util.UUID;

public record AutoIntegrationResponse(
    UUID id,
    UUID indexInfoId,
    String indexClassification,
    String indexName,
    boolean enabled
) {}
