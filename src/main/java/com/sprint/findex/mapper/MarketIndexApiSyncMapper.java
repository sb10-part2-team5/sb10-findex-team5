package com.sprint.findex.mapper;

import com.sprint.findex.dto.openapi.MarketIndexApiResponse.Item;
import com.sprint.findex.dto.sync.IndexDataSyncSource;
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
  IndexInfoSyncSource toInfoSource(Item item);

  @Mapping(source = "basDt", target = "baseDate", dateFormat = "yyyyMMdd")
  @Mapping(source = "mkp", target = "marketPrice")
  @Mapping(source = "clpr", target = "closingPrice")
  @Mapping(source = "hipr", target = "highPrice")
  @Mapping(source = "lopr", target = "lowPrice")
  @Mapping(source = "vs", target = "versus")
  @Mapping(source = "fltRt", target = "fluctuationRate")
  @Mapping(source = "trqu", target = "tradingQuantity")
  @Mapping(source = "trPrc", target = "tradingPrice")
  @Mapping(source = "lstgMrktTotAmt", target = "marketTotalAmount")
  IndexDataSyncSource toDataSource(Item item);
}
