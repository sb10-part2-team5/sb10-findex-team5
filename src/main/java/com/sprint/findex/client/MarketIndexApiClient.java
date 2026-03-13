package com.sprint.findex.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.findex.config.MarketIndexApiProperties;
import com.sprint.findex.dto.MarketIndexApiRequest;
import com.sprint.findex.dto.MarketIndexApiResponse;
import java.net.URI;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriBuilder;

@Component
@RequiredArgsConstructor
public class MarketIndexApiClient {

    private static final String PATH = "/getStockMarketIndex";

    private final MarketIndexApiProperties marketIndexApiProperties;
    private final ObjectMapper objectMapper;
    private final RestClient marketIndexRestClient;

    public MarketIndexApiResponse getMarketIndex(MarketIndexApiRequest request) {
        String rawBody = marketIndexRestClient.get()
                .uri(uriBuilder -> buildUri(uriBuilder, request))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(String.class);

        if (rawBody == null || rawBody.isBlank()) {
            // 예외 개선 필요
            throw new RuntimeException();
        }

        try {
            return objectMapper.readValue(rawBody, MarketIndexApiResponse.class);
        } catch (JsonProcessingException e) {
            // 예외 개선 필요
            throw new RuntimeException();
        }
    }

    private URI buildUri(UriBuilder uriBuilder, MarketIndexApiRequest request) {
        uriBuilder.path(PATH);
        uriBuilder.queryParam("serviceKey", marketIndexApiProperties.serviceKey());
        uriBuilder.queryParam("resultType", "json");

        for (Map.Entry<String, Object> entry : request.toQueryParams().entrySet()) {
            uriBuilder.queryParam(entry.getKey(), entry.getValue());
        }

        return uriBuilder.build();
    }
}
