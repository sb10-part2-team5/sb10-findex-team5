package com.sprint.findex.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.external-api")
public record MarketIndexApiProperties(
    @NotBlank(message = "Open API 요청 주소가 비어있습니다.")
    String baseUrl,

    @NotBlank(message = "서비스키가 없습니다.")
    String serviceKey
) {

}
