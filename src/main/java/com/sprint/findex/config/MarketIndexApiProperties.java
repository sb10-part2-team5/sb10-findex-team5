package com.sprint.findex.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.external-api")
public record MarketIndexApiProperties(
    // 예외 메시지 필요
    @NotBlank(message = "")
    String baseUrl,

    // 예외 메시지 필요
    @NotBlank(message = "")
    String serviceKey
) {

}