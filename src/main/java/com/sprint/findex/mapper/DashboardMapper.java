package com.sprint.findex.mapper;

import com.sprint.findex.dto.dashboard.IndexPerformanceDto;
import com.sprint.findex.entity.IndexData;
import java.math.BigDecimal;
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
}
