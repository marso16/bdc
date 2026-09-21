package com.capitalbanking.stage.config;

import com.capitalbanking.stage.shared.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        FieldError firstError = ex.getBindingResult().getFieldErrors().isEmpty()
                ? null
                : ex.getBindingResult().getFieldErrors().get(0);

        LOGGER.warn("Validation failed: {}", firstError != null
                ? firstError.getField() + " - " + firstError.getDefaultMessage() : "unknown");

        return error(Constants.ERR_CODE_399, firstError != null
                ? firstError.getField() + ": " + firstError.getDefaultMessage()
                : "Validation failed");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleUnreadable(HttpMessageNotReadableException ex) {
        LOGGER.warn("Malformed request body: {}", ex.getMessage());
        return error(Constants.ERR_CODE_399, "Malformed or missing request body");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> handleWrongMethod(HttpRequestMethodNotSupportedException ex) {
        LOGGER.warn("Method not supported: {}", ex.getMessage());
        return error(Constants.ERR_CODE_399, "HTTP method not supported: " + ex.getMethod());
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> handleWrongMediaType(HttpMediaTypeNotSupportedException ex) {
        LOGGER.warn("Media type not supported: {}", ex.getMessage());
        return error(Constants.ERR_CODE_399, "Content-Type not supported. Use application/json");
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> handleMissingParam(MissingServletRequestParameterException ex) {
        LOGGER.warn("Missing parameter: {}", ex.getParameterName());
        return error(Constants.ERR_CODE_399, "Missing required parameter: " + ex.getParameterName());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
        LOGGER.error("Unexpected error: ", ex);
        return error(Constants.ERR_CODE_500, Constants.ERR_MSG_500);
    }

    private ResponseEntity<Map<String, Object>> error(String code, String description) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", code);
        body.put("error_description", description);
        body.put("acquirertrxref", null);
        return ResponseEntity.badRequest().body(body);
    }
}