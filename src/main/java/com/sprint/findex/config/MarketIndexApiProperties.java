package com.sprint.findex.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.external-api")
public record MarketIndexApiProperties(
    String baseUrl,
    String serviceKey
) {

}
