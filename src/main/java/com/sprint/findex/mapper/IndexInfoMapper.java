package com.sprint.findex.mapper;

import com.sprint.findex.dto.indexinfo.IndexInfoDto;
import com.sprint.findex.entity.IndexInfo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IndexInfoMapper {

  IndexInfoDto toDto(IndexInfo indexInfo);
}
