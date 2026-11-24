package com.example.devspringbootpostgrespokemon.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ErrorResponse {

    private final Instant timestamp;
    private final int status;
    private final String error;
    private final String message;
    private final Map<String, String> fieldErrors;

    private ErrorResponse(HttpStatus status, String message, Map<String, String> fieldErrors) {
        this.timestamp = Instant.now();
        this.status = status.value();
        this.error = status.getReasonPhrase();
        this.message = message;
        this.fieldErrors = fieldErrors;
    }

    public static ErrorResponse of(HttpStatus status, String message) {
        return new ErrorResponse(status, message, Map.of());
    }

    public static ErrorResponse withFieldErrors(HttpStatus status, String message, Map<String, String> fieldErrors) {
        return new ErrorResponse(status, message, Map.copyOf(fieldErrors));
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }
}
