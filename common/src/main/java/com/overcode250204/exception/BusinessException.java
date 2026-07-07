package com.overcode250204.exception;

/**
 * BusinessException — Domain-level exception for BizLedger services.
 *
 * Use this when a business rule is violated (not a technical error).
 * The GlobalExceptionHandler in each service maps this to 400/409 responses.
 *
 * Examples:
 * throw new BusinessException("INSUFFICIENT_STOCK", "Not enough stock to
 * reserve");
 * throw new BusinessException("ORDER_NOT_DRAFT", "Only DRAFT orders can be
 * submitted");
 * throw new BusinessException("IDEMPOTENCY_CONFLICT", "Payment already
 * processed");
 */
public class BusinessException extends RuntimeException {

    private final String code;

    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String code() {
        return code;
    }
}
