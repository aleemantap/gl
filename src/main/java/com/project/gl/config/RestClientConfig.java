package com.project.gl.config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${gadai.api.base-url}")
    private String gadaiApiBaseUrl;

    @Bean
    public RestClient gadaiRestClient() {
        return RestClient.builder()
                .baseUrl(gadaiApiBaseUrl)
                .build();
    }
}