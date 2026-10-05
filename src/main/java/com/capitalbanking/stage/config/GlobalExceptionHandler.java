package com.capitalbanking.stage.config;

import com.capitalbanking.stage.shared.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
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

        String description = firstError != null
                ? firstError.getDefaultMessage()
                : "La requête contient des données invalides.";

        LOGGER.warn("Validation failed: {}", firstError != null
                ? firstError.getField() + " - " + firstError.getDefaultMessage() : "inconnu");

        return error(HttpStatus.BAD_REQUEST, Constants.ERR_CODE_399, description);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleUnreadable(HttpMessageNotReadableException ex) {
        LOGGER.warn("Malformed request body: {}", ex.getMessage());
        return error(HttpStatus.BAD_REQUEST, Constants.ERR_CODE_399,
                "Le corps de la requête est manquant ou mal formé (JSON invalide).");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> handleWrongMethod(HttpRequestMethodNotSupportedException ex) {
        LOGGER.warn("Method not supported: {}", ex.getMessage());
        return error(HttpStatus.BAD_REQUEST, Constants.ERR_CODE_399,
                "La méthode HTTP '" + ex.getMethod() + "' n'est pas supportée pour ce service.");
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> handleWrongMediaType(HttpMediaTypeNotSupportedException ex) {
        LOGGER.warn("Media type not supported: {}", ex.getMessage());
        return error(HttpStatus.BAD_REQUEST, Constants.ERR_CODE_399,
                "Le type de contenu n'est pas supporté. Utilisez application/json.");
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> handleMissingParam(MissingServletRequestParameterException ex) {
        LOGGER.warn("Missing parameter: {}", ex.getParameterName());
        return error(HttpStatus.BAD_REQUEST, Constants.ERR_CODE_399,
                "Le paramètre obligatoire '" + ex.getParameterName() + "' est manquant.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
        LOGGER.error("Unexpected error: ", ex);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, Constants.ERR_CODE_500, Constants.ERR_MSG_500);
    }

    private ResponseEntity<Map<String, Object>> error(HttpStatus status, String code, String description) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", code);
        body.put("error_description", description);
        body.put("acquirertrxref", null);
        return ResponseEntity.status(status).body(body);
    }
}
