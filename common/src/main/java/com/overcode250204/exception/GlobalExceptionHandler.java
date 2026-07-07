package com.overcode250204.exception;

import com.overcode250204.common.web.ApiResponse;
import com.overcode250204.exception.BusinessException;
import com.overcode250204.web.ApiError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * GlobalExceptionHandler — Shared REST error handler for all BizLedger
 * services.
 * ─────────────────────────────────────────────────────────────────────────────────
 * Each service imports this from common via @SpringBootApplication component
 * scan
 * or explicit @Import.
 *
 * Maps exceptions to consistent ApiResponse<Void> error bodies.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /** Domain rule violations → 400 Bad Request */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusiness(BusinessException ex) {
        log.warn("Business rule violation | code={} message={}", ex.code(), ex.getMessage());
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.failure(ex.getMessage(), ApiError.of(ex.code(), ex.getMessage())));
    }

    /** Bean validation failures → 422 Unprocessable Entity */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fe -> fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "Invalid value",
                        (a, b) -> a));
        log.warn("Validation failed | errors={}", fieldErrors);
        return ResponseEntity
                .unprocessableEntity()
                .body(ApiResponse.failure("Validation failed", ApiError.validation(fieldErrors)));
    }

    /** Bad credentials → 401 Unauthorized */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentials(BadCredentialsException ex) {
        log.warn("Authentication failed: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.failure("Authentication failed",
                        ApiError.of("INVALID_CREDENTIALS", ex.getMessage())));
    }

    /** Catch-all → 500 Internal Server Error */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneric(Exception ex) {
        log.error("Unhandled error", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.failure("Internal server error",
                        ApiError.of("INTERNAL_ERROR", "An unexpected error occurred")));
    }
}
