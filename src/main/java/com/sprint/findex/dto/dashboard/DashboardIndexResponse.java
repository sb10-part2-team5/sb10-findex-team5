package com.sprint.findex.dto.dashboard;

import java.math.BigDecimal;
import java.util.UUID;

public record DashboardIndexResponse(UUID indexInfoId,
                                     String indexClassification,
                                     String indexName,
                                     BigDecimal versus,
                                     BigDecimal fluctuationRate,
                                     BigDecimal currentPrice,
                                     BigDecimal beforePrice) {
}
