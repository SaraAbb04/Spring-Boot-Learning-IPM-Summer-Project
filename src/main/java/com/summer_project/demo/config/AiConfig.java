package com.summer_project.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class AiConfig {
    @Value("${ai.base-url}")
    private String baseUrl;
    @Value("${ai.api-key}")
    private String apiKey;
    @Bean
    public RestClient restClient() {
        return RestClient.builder().baseUrl(baseUrl).defaultHeader("Authorization", "Bearer " + apiKey).defaultHeader("Content-Type", "application/json").build();
    }
}
