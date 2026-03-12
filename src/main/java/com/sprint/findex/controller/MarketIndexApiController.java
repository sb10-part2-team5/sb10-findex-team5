package com.sprint.findex.controller;

import com.sprint.findex.dto.MarketIndexApiRequest;
import com.sprint.findex.dto.MarketIndexApiResponse;
import com.sprint.findex.service.MarketIndexApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MarketIndexApiController {

  private final MarketIndexApiService marketIndexApiService;

  // 사용 예시
  @GetMapping("/api/index")
  public MarketIndexApiResponse getMarketIndex() {
    MarketIndexApiRequest request = MarketIndexApiRequest.builder()
        .idxNm("KRX 리츠 TOP 10 지수")
        .build();

    return marketIndexApiService.getMarketIndex(request);
  }
}
