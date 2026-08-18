package com.physioos.common.exception;

import io.micrometer.tracing.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final Tracer tracer;

    public GlobalExceptionHandler(Tracer tracer) {
        this.tracer = tracer;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAllExceptions(Exception ex) {
        String traceId = tracer.currentSpan() != null ? tracer.currentSpan().context().traceId() : "N/A";
        
        log.error("Unhandled exception occurred. TraceId: {}", traceId, ex);

        ApiError apiError = new ApiError(
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred.",
                traceId
        );
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String traceId = tracer.currentSpan() != null ? tracer.currentSpan().context().traceId() : "N/A";
        
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce((a, b) -> a + ", " + b)
                .orElse("Validation failed");

        log.warn("Validation exception occurred. TraceId: {} - {}", traceId, errorMessage);

        ApiError apiError = new ApiError(
                "VALIDATION_ERROR",
                errorMessage,
                traceId
        );
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    // Additional specific handlers (e.g. EntityNotFoundException) can be added here
}
