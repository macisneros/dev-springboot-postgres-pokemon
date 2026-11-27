package com.example.devspringbootpostgrespokemon.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** Mensaje genérico: no revela si falló el usuario o la contraseña. */
    private static final String INVALID_CREDENTIALS = "Invalid username or password";

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(AuthenticationException ex) {
        log.debug("Authentication failed: {}", ex.getMessage());
        return toResponse(ErrorResponse.of(HttpStatus.UNAUTHORIZED, INVALID_CREDENTIALS));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> Objects.requireNonNullElse(error.getDefaultMessage(), "invalid value"),
                        (first, second) -> first));
        return toResponse(ErrorResponse.withFieldErrors(HttpStatus.BAD_REQUEST, "Validation failed", fieldErrors));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadableBody(HttpMessageNotReadableException ex) {
        return toResponse(ErrorResponse.of(HttpStatus.BAD_REQUEST, "Malformed request body"));
    }

    private static ResponseEntity<ErrorResponse> toResponse(ErrorResponse body) {
        return ResponseEntity.status(body.getStatus()).body(body);
    }
}
