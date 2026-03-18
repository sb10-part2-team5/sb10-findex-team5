package com.sprint.findex.client;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.findex.dto.openapi.MarketIndexApiRequest;
import com.sprint.findex.dto.openapi.MarketIndexApiResponse;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MarketIndexApiCacheServiceTest {

    private final MarketIndexApiCacheService cacheService = new MarketIndexApiCacheService();

    @Test
    @DisplayName("같은 과거 날짜 OpenAPI 요청은 캐시된 응답을 재사용")
    void getOrLoad_reusesCachedResponseForSameHistoricalRequest() {
        MarketIndexApiRequest request = MarketIndexApiRequest.builder()
                .basDt("20260317")
                .pageNo(1)
                .numOfRows(100)
                .build();
        AtomicInteger loadCount = new AtomicInteger();

        MarketIndexApiResponse first = cacheService.getOrLoad(request, () -> {
            loadCount.incrementAndGet();
            return response("1");
        });

        MarketIndexApiResponse second = cacheService.getOrLoad(request, () -> {
            loadCount.incrementAndGet();
            return response("2");
        });

        assertThat(loadCount.get()).isEqualTo(1);
        assertThat(second).isSameAs(first);
    }

    @Test
    @DisplayName("조건이 다른 OpenAPI 요청은 서로 다른 캐시 키를 사용")
    void getOrLoad_usesDifferentCacheKeyForDifferentRequest() {
        AtomicInteger loadCount = new AtomicInteger();

        MarketIndexApiResponse first = cacheService.getOrLoad(
                MarketIndexApiRequest.builder()
                        .basDt("20260317")
                        .pageNo(1)
                        .numOfRows(100)
                        .build(),
                () -> {
                    loadCount.incrementAndGet();
                    return response("1");
                }
        );

        MarketIndexApiResponse second = cacheService.getOrLoad(
                MarketIndexApiRequest.builder()
                        .basDt("20260318")
                        .pageNo(1)
                        .numOfRows(100)
                        .build(),
                () -> {
                    loadCount.incrementAndGet();
                    return response("2");
                }
        );

        assertThat(loadCount.get()).isEqualTo(2);
        assertThat(second).isNotSameAs(first);
    }

    private MarketIndexApiResponse response(String totalCount) {
        return new MarketIndexApiResponse(
                new MarketIndexApiResponse.Response(
                        new MarketIndexApiResponse.Header("00", "OK"),
                        new MarketIndexApiResponse.Body(
                                "100",
                                "1",
                                totalCount,
                                new MarketIndexApiResponse.Items(null)
                        )
                )
        );
    }
}
