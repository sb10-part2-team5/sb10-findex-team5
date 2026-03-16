package com.sprint.findex.mapper;

import com.sprint.findex.dto.indexdata.IndexDataDto;
import com.sprint.findex.entity.IndexData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface IndexDataMapper {

    @Mapping(target = "indexInfoId", source = "indexInfo.id")
    IndexDataDto toDto(IndexData indexData);

}
