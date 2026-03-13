package com.sprint.findex.mapper;

import com.sprint.findex.dto.autosyncconfig.AutoSyncConfigResponse;
import com.sprint.findex.entity.AutoSyncConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AutoSyncConfigMapper {

    @Mapping(source = "indexInfo.id", target = "indexInfoId")
    @Mapping(source = "indexInfo.indexClassification", target = "indexClassification")
    @Mapping(source = "indexInfo.indexName", target = "indexName")
    AutoSyncConfigResponse toResponse(AutoSyncConfig autoSyncConfig);
}
