package com.sprint.findex.mapper;

import com.sprint.findex.dto.sync.SyncJobDto;
import com.sprint.findex.entity.IntegrationTask;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SyncJobMapper {

  @Mapping(source = "indexInfo.id", target = "indexInfoId")
  SyncJobDto toDto(IntegrationTask integrationTask);

  default LocalDateTime map(Instant value) {
    return LocalDateTime.ofInstant(value, ZoneOffset.UTC);
  }
}
