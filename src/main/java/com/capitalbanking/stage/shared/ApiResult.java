package com.capitalbanking.stage.shared;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public final class ApiResult<T> {

    private final HttpStatus status;
    private final T body;

    private ApiResult(HttpStatus status, T body) {
        this.status = status;
        this.body = body;
    }

    public static <T> ApiResult<T> ok(T body) {
        return new ApiResult<>(HttpStatus.OK, body);
    }

    public static <T> ApiResult<T> badRequest(T body) {
        return new ApiResult<>(HttpStatus.BAD_REQUEST, body);
    }

    public static <T> ApiResult<T> status(HttpStatus status, T body) {
        return new ApiResult<>(status, body);
    }

    public HttpStatus status() {
        return status;
    }

    public ResponseEntity<T> toResponseEntity() {
        return ResponseEntity.status(status).body(body);
    }
}
