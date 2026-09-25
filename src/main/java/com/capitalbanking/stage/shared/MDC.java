package com.capitalbanking.stage.shared;

import org.springframework.http.ResponseEntity;

import java.util.function.Supplier;

public class MDC {

    public static <T> ResponseEntity<T> withMdc(String key, String value, Supplier<ApiResult<T>> operation) {
        org.slf4j.MDC.put(key, key + ":" + value);
        try {
            return operation.get().toResponseEntity();
        } finally {
            org.slf4j.MDC.remove(key);
        }
    }
}