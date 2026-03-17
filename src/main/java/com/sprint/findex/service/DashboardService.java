package com.sprint.findex.service;

import com.sprint.findex.dto.dashboard.IndexChartDto;
import com.sprint.findex.dto.dashboard.IndexPerformanceDto;
import com.sprint.findex.dto.dashboard.RankedIndexPerformanceDto;
import com.sprint.findex.entity.IndexData;
import com.sprint.findex.entity.IndexInfo;
import com.sprint.findex.enums.ChartPeriodType;
import com.sprint.findex.enums.PeriodType;
import com.sprint.findex.exception.BusinessLogicException;
import com.sprint.findex.exception.ExceptionCode;
import com.sprint.findex.mapper.DashboardMapper;
import com.sprint.findex.repository.DashboardRepository;
import com.sprint.findex.repository.IndexDataRepository;
import com.sprint.findex.repository.IndexInfoRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
    private static final int RATE_SCALE = 2;
    private static final int MA5_PERIOD = 5;
    private static final int MA20_PERIOD = 20;

    private final DashboardRepository dashboardRepository;
    private final DashboardMapper dashboardMapper;
    private final IndexInfoRepository indexInfoRepository;
    private final IndexDataRepository indexDataRepository;

    public List<IndexPerformanceDto> getFavoriteIndexPerformance(PeriodType periodType) {
        return dashboardRepository.findLatestFavoriteIndexData().stream()
                .map(indexData -> toPerformanceDto(indexData, periodType))
                .toList();
    }

    public List<RankedIndexPerformanceDto> getRankedIndexPerformance(UUID indexInfoId, PeriodType periodType, int limit) {
        List<IndexPerformanceDto> performances = dashboardRepository.findLatestIndexData().stream()
                .map(indexData -> toPerformanceDto(indexData, periodType))
                .sorted(Comparator.comparing(IndexPerformanceDto::fluctuationRate).reversed())
                .toList();

        List<RankedIndexPerformanceDto> rankedPerformances = new ArrayList<>(performances.size());
        for (int i = 0; i < performances.size(); i++) {
            rankedPerformances.add(new RankedIndexPerformanceDto(performances.get(i), i + 1));
        }

        if (indexInfoId != null) {
            return rankedPerformances.stream()
                    .filter(rankedPerformance -> rankedPerformance.performance().indexInfoId().equals(indexInfoId))
                    .toList();
        }

        return rankedPerformances.stream()
                .limit(limit)
                .toList();
    }

    public IndexChartDto getIndexChart(UUID indexInfoId, ChartPeriodType periodType) {
        IndexInfo indexInfo = indexInfoRepository.findById(indexInfoId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.INDEX_INFO_NOT_FOUND));

        LocalDate latestBaseDate = indexDataRepository
                .findLatestByIndexInfoId(indexInfoId)
                .map(IndexData::getBaseDate)
                .orElse(null);

        ChartSeries chartSeries = buildChartSeries(indexInfoId, latestBaseDate, periodType);

        return dashboardMapper.toIndexChartDto(
                indexInfo,
                periodType,
                chartSeries.dataPoints(),
                chartSeries.ma5DataPoints(),
                chartSeries.ma20DataPoints()
        );
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
                .findNearestByIndexInfoIdFromBaseDate(
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

    private LocalDate resolveStartDate(LocalDate latestBaseDate, ChartPeriodType periodType) {
        return switch (periodType) {
            case MONTHLY -> latestBaseDate.minusMonths(1);
            case QUARTERLY -> latestBaseDate.minusMonths(3);
            case YEARLY -> latestBaseDate.minusYears(1);
        };
    }

    private ChartSeries buildChartSeries(
            UUID indexInfoId,
            LocalDate latestBaseDate,
            ChartPeriodType periodType
    ) {
        if (latestBaseDate == null) {
            return new ChartSeries(List.of(), List.of(), List.of());
        }

        LocalDate startDate = resolveStartDate(latestBaseDate, periodType);

        List<IndexData> rows = indexDataRepository
                .findChartDataByIndexInfoId(indexInfoId, startDate);

        List<IndexChartDto.ChartDataPoint> dataPoints = dashboardMapper.toChartDataPoints(rows);

        return new ChartSeries(
                dataPoints,
                calculateMovingAverage(dataPoints, MA5_PERIOD),
                calculateMovingAverage(dataPoints, MA20_PERIOD)
        );
    }

    private List<IndexChartDto.ChartDataPoint> calculateMovingAverage(
            List<IndexChartDto.ChartDataPoint> dataPoints,
            int period
    ) {
        List<IndexChartDto.ChartDataPoint> result = new ArrayList<>();
        BigDecimal windowSum = BigDecimal.ZERO;

        for (int i = 0; i < dataPoints.size(); i++) {
            windowSum = windowSum.add(dataPoints.get(i).value());

            if (i >= period) {
                windowSum = windowSum.subtract(dataPoints.get(i - period).value());
            }

            if (i >= period - 1) {
                BigDecimal average = windowSum.divide(
                        BigDecimal.valueOf(period),
                        RATE_SCALE,
                        RoundingMode.HALF_UP
                );

                result.add(new IndexChartDto.ChartDataPoint(
                        dataPoints.get(i).date(),
                        average
                ));
            }
        }
        return result;
    }

    private record ChartSeries(
            List<IndexChartDto.ChartDataPoint> dataPoints,
            List<IndexChartDto.ChartDataPoint> ma5DataPoints,
            List<IndexChartDto.ChartDataPoint> ma20DataPoints
    ) {
    }
}
