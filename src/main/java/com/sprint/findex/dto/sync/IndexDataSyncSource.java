package com.sprint.findex.dto.sync;

import java.math.BigDecimal;
import java.time.LocalDate;

public record IndexDataSyncSource(
        LocalDate baseDate,
        BigDecimal marketPrice,
        BigDecimal closingPrice,
        BigDecimal highPrice,
        BigDecimal lowPrice,
        BigDecimal versus,
        BigDecimal fluctuationRate,
        Long tradingQuantity,
        Long tradingPrice,
        Long marketTotalAmount
) {

}
