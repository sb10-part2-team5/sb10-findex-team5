package com.sprint.findex.dto.openapi;

import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MarketIndexApiRequest {

  private Integer pageNo;
  private Integer numOfRows;
  private String basDt;
  private String beginBasDt;
  private String endBasDt;
  private String likeBasDt;
  private String idxNm;
  private String likeIdxNm;
  private String beginEpyItmsCnt;
  private String endEpyItmsCnt;
  private String beginFltRt;
  private String endFltRt;
  private String beginTrqu;
  private String endTrqu;
  private String beginTrPrc;
  private String endTrPrc;
  private String beginLstgMrktTotAmt;
  private String endLstgMrktTotAmt;
  private String beginLsYrEdVsFltRg;
  private String endLsYrEdVsFltRg;
  private String beginLsYrEdVsFltRt;
  private String endLsYrEdVsFltRt;

  public Map<String, Object> toQueryParams() {
    Map<String, Object> params = new LinkedHashMap<>();

    putIfPresent(params, "pageNo", pageNo);
    putIfPresent(params, "numOfRows", numOfRows);
    putIfPresent(params, "basDt", basDt);
    putIfPresent(params, "beginBasDt", beginBasDt);
    putIfPresent(params, "endBasDt", endBasDt);
    putIfPresent(params, "likeBasDt", likeBasDt);
    putIfPresent(params, "idxNm", idxNm);
    putIfPresent(params, "likeIdxNm", likeIdxNm);
    putIfPresent(params, "beginEpyItmsCnt", beginEpyItmsCnt);
    putIfPresent(params, "endEpyItmsCnt", endEpyItmsCnt);
    putIfPresent(params, "beginFltRt", beginFltRt);
    putIfPresent(params, "endFltRt", endFltRt);
    putIfPresent(params, "beginTrqu", beginTrqu);
    putIfPresent(params, "endTrqu", endTrqu);
    putIfPresent(params, "beginTrPrc", beginTrPrc);
    putIfPresent(params, "endTrPrc", endTrPrc);
    putIfPresent(params, "beginLstgMrktTotAmt", beginLstgMrktTotAmt);
    putIfPresent(params, "endLstgMrktTotAmt", endLstgMrktTotAmt);
    putIfPresent(params, "beginLsYrEdVsFltRg", beginLsYrEdVsFltRg);
    putIfPresent(params, "endLsYrEdVsFltRg", endLsYrEdVsFltRg);
    putIfPresent(params, "beginLsYrEdVsFltRt", beginLsYrEdVsFltRt);
    putIfPresent(params, "endLsYrEdVsFltRt", endLsYrEdVsFltRt);

    return params;
  }

  private void putIfPresent(Map<String, Object> params, String key, Object value) {
    if (value == null) {
      return;
    }
    if (value instanceof String stringValue && stringValue.isBlank()) {
      return;
    }
    params.put(key, value);
  }
}
