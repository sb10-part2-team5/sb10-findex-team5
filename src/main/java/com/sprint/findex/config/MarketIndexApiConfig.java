package com.sprint.findex.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(MarketIndexApiProperties.class)
public class MarketIndexApiConfig {

    @Bean
    public RestClient marketIndexRestClient(MarketIndexApiProperties properties) {
        return RestClient.builder()
                .baseUrl(properties.baseUrl())
                .build();
    }
}
