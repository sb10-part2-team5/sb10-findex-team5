package com.sprint.findex.dto.openapi;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.List;

public record MarketIndexApiResponse(
    Response response
) {

  public record Response(
      Header header,
      Body body
  ) {

  }

  public record Header(
      String resultCode,
      String resultMsg
  ) {

  }

  public record Body(
      String numOfRows,
      String pageNo,
      String totalCount,
      Items items
  ) {

    public List<Item> itemList() {
      if (items == null || items.item() == null) {
        return List.of();
      }
      return items.item();
    }
  }

  public record Items(
      List<Item> item
  ) {

  }

  public record Item(
      String lsYrEdVsFltRt,
      String basPntm,
      String basIdx,
      String basDt,
      String idxCsf,
      String idxNm,
      String epyItmsCnt,
      String clpr,
      String vs,
      String fltRt,
      String mkp,
      String hipr,
      String lopr,
      String trqu,
      String trPrc,
      String lstgMrktTotAmt,
      String lsYrEdVsFltRg,
      String yrWRcrdHgst,
      String yrWRcrdHgstDt,
      String yrWRcrdLwst,
      String yrWRcrdLwstDt
  ) {

  }
}
