package com.sprint.findex.service;

import com.sprint.findex.dto.dashboard.IndexPerformanceDto;
import com.sprint.findex.entity.IndexData;
import com.sprint.findex.enums.PeriodType;
import com.sprint.findex.mapper.DashboardMapper;
import com.sprint.findex.repository.DashboardRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
    private static final int RATE_SCALE = 2;

    private final DashboardRepository dashboardRepository;
    private final DashboardMapper dashboardMapper;

    public List<IndexPerformanceDto> getFavoriteIndexPerformance(PeriodType periodType) {
        return dashboardRepository.findLatestFavoriteIndexData().stream()
                .map(indexData -> toPerformanceDto(indexData, periodType))
                .toList();
    }

    private IndexPerformanceDto toPerformanceDto(IndexData data, PeriodType periodType) {
        BigDecimal beforePrice = resolveBeforePrice(data, periodType);
        BigDecimal versus = data.getClosingPrice().subtract(beforePrice);
        BigDecimal fluctuationRate = calculateFluctuationRate(versus, beforePrice);

        return dashboardMapper.toDto(data, beforePrice, versus, fluctuationRate);
    }

    private BigDecimal resolveBeforePrice(IndexData data, PeriodType periodType) {
        if (periodType == PeriodType.DAILY) {
            return data.getClosingPrice().subtract(data.getVersus());
        }

        LocalDate targetDate = getTargetDate(data.getBaseDate(), periodType);
        return dashboardRepository
                .findTopByIndexInfoIdAndBaseDateLessThanEqualOrderByBaseDateDesc(
                        data.getIndexInfo().getId(),
                        targetDate
                )
                .map(IndexData::getClosingPrice)
                .orElseGet(() -> data.getClosingPrice().subtract(data.getVersus()));
    }

    private LocalDate getTargetDate(LocalDate baseDate, PeriodType periodType) {
        return switch (periodType) {
            case DAILY -> baseDate.minusDays(1);
            case WEEKLY -> baseDate.minusWeeks(1);
            case MONTHLY -> baseDate.minusMonths(1);
        };
    }

    private BigDecimal calculateFluctuationRate(BigDecimal versus, BigDecimal beforePrice) {
        if (beforePrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return versus.multiply(HUNDRED)
                .divide(beforePrice, RATE_SCALE, RoundingMode.HALF_UP);
    }
}
