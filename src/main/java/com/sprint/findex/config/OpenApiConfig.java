package com.sprint.findex.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Value("${findex.server-url}")
    private String serverUrl;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Findex API")
                        .description("가볍고 빠른 외부 API 연동 금융 분석 도구 API 문서")
                )
                .addServersItem(new Server()
                    .url(serverUrl).description("현재 환경 서버"));
    }
}
