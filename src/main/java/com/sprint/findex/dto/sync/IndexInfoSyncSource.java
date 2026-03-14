package com.sprint.findex.dto.sync;

import java.math.BigDecimal;
import java.time.LocalDate;

public record IndexInfoSyncSource(
    String indexName,
    String indexClassification,
    Integer employedItemsCount,
    LocalDate basePointInTime,
    BigDecimal baseIndex
) {

}
