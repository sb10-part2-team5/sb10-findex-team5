package com.sprint.findex.mapper;

import com.sprint.findex.dto.dashboard.IndexChartDto;
import com.sprint.findex.dto.dashboard.IndexPerformanceDto;
import com.sprint.findex.entity.IndexData;
import com.sprint.findex.entity.IndexInfo;
import com.sprint.findex.enums.ChartPeriodType;
import java.math.BigDecimal;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DashboardMapper {
    @Mapping(source = "data.indexInfo.id", target = "indexInfoId")
    @Mapping(source = "data.indexInfo.indexClassification", target = "indexClassification")
    @Mapping(source = "data.indexInfo.indexName", target = "indexName")
    @Mapping(source = "data.closingPrice", target = "currentPrice")
    @Mapping(source = "versus", target = "versus")
    @Mapping(source = "fluctuationRate", target = "fluctuationRate")
    IndexPerformanceDto toDto(
            IndexData data,
            BigDecimal beforePrice,
            BigDecimal versus,
            BigDecimal fluctuationRate
    );

    @Mapping(source = "indexInfo.id", target = "indexInfoId")
    @Mapping(source = "indexInfo.indexClassification", target = "indexClassification")
    @Mapping(source = "indexInfo.indexName", target = "indexName")
    @Mapping(source = "periodType", target = "periodType")
    @Mapping(source = "dataPoints", target = "dataPoints")
    @Mapping(source = "ma5DataPoints", target = "ma5DataPoints")
    @Mapping(source = "ma20DataPoints", target = "ma20DataPoints")
    IndexChartDto toIndexChartDto(
            IndexInfo indexInfo,
            ChartPeriodType periodType,
            List<IndexChartDto.ChartDataPoint> dataPoints,
            List<IndexChartDto.ChartDataPoint> ma5DataPoints,
            List<IndexChartDto.ChartDataPoint> ma20DataPoints
    );

    @Mapping(source = "baseDate", target = "date")
    @Mapping(source = "closingPrice", target = "value")
    IndexChartDto.ChartDataPoint toChartDataPoint(IndexData data);

    List<IndexChartDto.ChartDataPoint> toChartDataPoints(List<IndexData> rows);
}
