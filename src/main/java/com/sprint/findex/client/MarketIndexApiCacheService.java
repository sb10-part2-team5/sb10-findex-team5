package com.sprint.findex.client;

import com.sprint.findex.dto.openapi.MarketIndexApiRequest;
import com.sprint.findex.dto.openapi.MarketIndexApiResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import org.springframework.stereotype.Component;

@Component
public class MarketIndexApiCacheService {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");
    private static final DateTimeFormatter BASIC_DATE_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;
    private static final Duration TODAY_TTL = Duration.ofHours(1);
    private static final Duration HISTORICAL_TTL = Duration.ofDays(7);

    private final ConcurrentHashMap<String, CacheEntry> cache = new ConcurrentHashMap<>();

    public MarketIndexApiResponse getOrLoad(
            MarketIndexApiRequest request,
            Supplier<MarketIndexApiResponse> loader
    ) {
        String cacheKey = buildCacheKey(request);
        long now = System.currentTimeMillis();

        CacheEntry cached = cache.get(cacheKey);
        if (cached != null && !cached.isExpired(now)) {
            return cached.response();
        }

        MarketIndexApiResponse response = loader.get();
        long expiresAt = now + resolveTtl(request).toMillis();
        cache.put(cacheKey, new CacheEntry(response, expiresAt));
        return response;
    }

    private String buildCacheKey(MarketIndexApiRequest request) {
        StringJoiner joiner = new StringJoiner("&");

        for (Map.Entry<String, Object> entry : request.toQueryParams().entrySet()) {
            joiner.add(entry.getKey() + "=" + entry.getValue());
        }

        return joiner.toString();
    }

    private Duration resolveTtl(MarketIndexApiRequest request) {
        LocalDate latestRequestedDate = resolveLatestRequestedDate(request);
        if (latestRequestedDate == null) {
            return TODAY_TTL;
        }

        LocalDate today = LocalDate.now(KST);
        return latestRequestedDate.isBefore(today) ? HISTORICAL_TTL : TODAY_TTL;
    }

    private LocalDate resolveLatestRequestedDate(MarketIndexApiRequest request) {
        if (request.getBasDt() != null) {
            return LocalDate.parse(request.getBasDt(), BASIC_DATE_FORMATTER);
        }

        if (request.getEndBasDt() != null) {
            return LocalDate.parse(request.getEndBasDt(), BASIC_DATE_FORMATTER).minusDays(1);
        }

        if (request.getBeginBasDt() != null) {
            return LocalDate.parse(request.getBeginBasDt(), BASIC_DATE_FORMATTER);
        }

        return null;
    }

    private record CacheEntry(MarketIndexApiResponse response, long expiresAt) {

        private boolean isExpired(long now) {
            return expiresAt <= now;
        }
    }
}
