package com.sprint.findex.mapper;

import com.sprint.findex.dto.openapi.MarketIndexApiResponse.Item;
import com.sprint.findex.dto.sync.IndexInfoSyncSource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MarketIndexApiSyncMapper {

  @Mapping(source = "idxNm", target = "indexName")
  @Mapping(source = "idxCsf", target = "indexClassification")
  @Mapping(source = "epyItmsCnt", target = "employedItemsCount")
  @Mapping(source = "basPntm", target = "basePointInTime", dateFormat = "yyyyMMdd")
  @Mapping(source = "basIdx", target = "baseIndex")
  IndexInfoSyncSource toSource(Item item);
}
