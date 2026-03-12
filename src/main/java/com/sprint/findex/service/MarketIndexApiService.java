package com.sprint.findex.service;

import com.sprint.findex.client.MarketIndexApiClient;
import com.sprint.findex.dto.MarketIndexApiRequest;
import com.sprint.findex.dto.MarketIndexApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MarketIndexApiService {

  private final MarketIndexApiClient marketIndexApiClient;

  public MarketIndexApiResponse getMarketIndex(MarketIndexApiRequest request) {
    return marketIndexApiClient.getMarketIndex(request);
  }
}
