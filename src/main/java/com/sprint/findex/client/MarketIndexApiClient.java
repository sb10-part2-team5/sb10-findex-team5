package com.sprint.findex.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.findex.config.MarketIndexApiProperties;
import com.sprint.findex.dto.openapi.MarketIndexApiRequest;
import com.sprint.findex.dto.openapi.MarketIndexApiResponse;
import com.sprint.findex.exception.BusinessLogicException;
import com.sprint.findex.exception.ExceptionCode;
import java.net.URI;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.util.UriBuilder;

@Component
@RequiredArgsConstructor
public class MarketIndexApiClient {

    // 주가지수시세 조회 엔드포인트 경로
    private static final String PATH = "/getStockMarketIndex";
    // 응답 포맷 json 고정
    private static final String RESULT_TYPE = "json";
    // 공공데이터포털의 Open API 표준 성공 응답 코드
    private static final String SUCCESS_RESULT_CODE = "00";

    private final MarketIndexApiProperties marketIndexApiProperties;
    private final ObjectMapper objectMapper;
    private final RestClient marketIndexRestClient;
    private final MarketIndexApiCacheService marketIndexApiCacheService;

    public MarketIndexApiResponse getMarketIndex(MarketIndexApiRequest request) {
        return marketIndexApiCacheService.getOrLoad(request, () -> {
            String rawBody = requestRawBody(request);
            MarketIndexApiResponse response = parseResponse(rawBody);
            validateApiResponse(response);
            return response;
        });
    }

    private String requestRawBody(MarketIndexApiRequest request) {
        try {
            // 실제 요청 자체를 서버 상태 확인으로 간주하고, 요청 실패에 대한 처리를 한다.
            return marketIndexRestClient.get()
                    .uri(uriBuilder -> buildUri(uriBuilder, request))
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (clientRequest, clientResponse) -> {
                        throw new BusinessLogicException(ExceptionCode.OPEN_API_CLIENT_ERROR);
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (clientRequest, clientResponse) -> {
                        throw new BusinessLogicException(ExceptionCode.OPEN_API_SERVER_ERROR);
                    })
                    .body(String.class);
        } catch (RestClientResponseException e) {
            throw new BusinessLogicException(ExceptionCode.OPEN_API_INVALID_RESPONSE);
        } catch (RestClientException e) {
            throw new BusinessLogicException(ExceptionCode.OPEN_API_CONNECTION_ERROR);
        }
    }

    private URI buildUri(UriBuilder uriBuilder, MarketIndexApiRequest request) {
        uriBuilder.path(PATH);
        uriBuilder.queryParam("serviceKey", marketIndexApiProperties.serviceKey());
        uriBuilder.queryParam("resultType", RESULT_TYPE);

        for (Map.Entry<String, Object> entry : request.toQueryParams().entrySet()) {
            uriBuilder.queryParam(entry.getKey(), entry.getValue());
        }

        return uriBuilder.build();
    }

    private MarketIndexApiResponse parseResponse(String rawBody) {
        if (rawBody == null || rawBody.isBlank()) {
            throw new BusinessLogicException(ExceptionCode.OPEN_API_INVALID_RESPONSE);
        }

        try {
            return objectMapper.readValue(rawBody, MarketIndexApiResponse.class);
        } catch (JsonProcessingException e) {
            throw new BusinessLogicException(ExceptionCode.OPEN_API_RESPONSE_PARSING_ERROR);
        }
    }

    private void validateApiResponse(MarketIndexApiResponse response) {
        if (response == null || response.response() == null || response.response().header() == null
                || response.response().header().resultCode() == null) {
            throw new BusinessLogicException(ExceptionCode.OPEN_API_INVALID_RESPONSE);
        }

        String resultCode = response.response().header().resultCode();

        if (!SUCCESS_RESULT_CODE.equals(resultCode)) {
            throw new BusinessLogicException(ExceptionCode.OPEN_API_RESULT_ERROR);
        }
    }
}
