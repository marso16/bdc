package com.capitalbanking.stage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
@EnableConfigurationProperties
public class EntryPoint {

    public static void main(String[] args) {
        SpringApplication.run(EntryPoint.class, args);
    }
}