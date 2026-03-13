package com.sprint.findex.mapper;

import com.sprint.findex.dto.autointegration.AutoIntegrationResponse;
import com.sprint.findex.entity.AutoIntegration;
import com.sprint.findex.entity.IndexInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AutoIntegrationMapper {

    @Mapping(source = "indexInfo.id", target = "indexInfoId")
    @Mapping(source = "indexInfo.indexClassification", target = "indexClassification")
    @Mapping(source = "indexInfo.indexName", target = "indexName")
    AutoIntegrationResponse toResponse(AutoIntegration autoIntegration);
}
