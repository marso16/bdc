package com.capitalbanking.stage.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.HashSet;

@Configuration
public class HttpLoggingConfig {

    @Bean
    @ConditionalOnProperty(prefix = "http.log", name = "enabled", havingValue = "true", matchIfMissing = true)
    public HttpLoggingFilter httpLoggingFilter() {
        return new HttpLoggingFilter(
                4000,
                new HashSet<>(Arrays.asList("/actuator", "/health", "/static", "/webjars")),
                new HashSet<>(Arrays.asList("password", "authorization", "token", "secret", "apiKey", "apikey"))
        );
    }
}