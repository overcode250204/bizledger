package com.overcode250204.identityservice.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @AuditLog — Method-level annotation for audit trail.
 *           ─────────────────────────────────────────────────────
 *           Place on any service or controller method that represents a
 *           significant user action (login, register, role-change, etc.).
 *
 *           The AuditLogAspect will intercept the call and emit a structured
 *           log line including: action, resource, userId, tenantId, traceId,
 *           outcome.
 *
 *           Example usage:
 *
 *           <pre>
 *   {@literal @}AuditLog(action = "USER_LOGIN", resource = "auth")
 *   public AuthResponse login(LoginRequest req) { ... }
 * </pre>
 *
 *           Interview note:
 *           "AOP-based audit logging keeps audit concerns completely out of
 *           business logic. Service methods don't need to know they're being
 *           audited."
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuditLog {

    /**
     * Business action being performed.
     * Recommended format: SCREAMING_SNAKE_CASE.
     * Examples: USER_LOGIN, TENANT_REGISTERED, ROLE_ASSIGNED, USER_LOCKED
     */
    String action();

    /**
     * Resource/domain being acted on.
     * Examples: "auth", "user", "role", "permission"
     */
    String resource();

    /**
     * Extra description (optional, human-readable detail).
     */
    String description() default "";
}

