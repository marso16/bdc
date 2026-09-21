package com.capitalbanking.stage.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RequiredArgsConstructor
@Configuration
public class RegisterRiCommonsApiCallsInterceptor implements WebMvcConfigurer {

    private final RiCommonsApiCallsInterceptor riCommonsApiCallsInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(riCommonsApiCallsInterceptor)
                .addPathPatterns("/**");
    }
}