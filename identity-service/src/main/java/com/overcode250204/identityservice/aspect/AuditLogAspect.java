package com.overcode250204.identityservice.aspect;

import com.overcode250204.identityservice.annotation.AuditLog;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * AuditLogAspect — AOP-based audit trail interceptor
 * ─────────────────────────────────────────────────────
 * Intercepts all methods annotated with {@literal @}AuditLog and emits
 * a structured audit event to the application log.
 *
 * Layered architecture role:
 * This is a CROSS-CUTTING CONCERN in the infrastructure layer.
 * It sits between the controller and service layers transparently.
 *
 * What it logs:
 * - ACTION: the business action (e.g. USER_LOGIN)
 * - RESOURCE: domain affected (e.g. "auth")
 * - userId, tenantId from MDC (injected by JwtAuthFilter or request header)
 * - traceId from MDC (injected by gateway LoggingFilter via X-Trace-Id header)
 * - outcome: SUCCESS or FAILURE (+ exception class on failure)
 * - durationMs: execution time
 * - occurredAt: ISO-8601 timestamp
 *
 * Architecture decision:
 * We use @Around (not @AfterReturning + @AfterThrowing) because we need
 * to measure duration AND capture both success and failure in a single place.
 *
 * Interview note:
 * "Spring AOP lets us inject cross-cutting audit concerns without polluting
 * business logic. AuthService.login() doesn't know it's being audited — the
 * aspect handles it. This is the Open/Closed Principle in practice."
 */
@Aspect
@Component
public class AuditLogAspect {

    private static final Logger auditLog = LoggerFactory.getLogger("AUDIT");

    /**
     * Intercept any method annotated with @AuditLog anywhere in the application.
     */
    @Around("@annotation(com.overcode250204.identityservicejava.annotation.AuditLog)")
    public Object auditAroundMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        AuditLog annotation = methodSignature.getMethod().getAnnotation(AuditLog.class);

        String action = annotation.action();
        String resource = annotation.resource();
        String description = annotation.description();
        String methodName = methodSignature.getDeclaringType().getSimpleName()
                + "." + methodSignature.getName();

        // Pull identity context from MDC (populated by JwtAuthenticationFilter)
        String userId = MDC.get("userId");
        String tenantId = MDC.get("tenantId");
        String traceId = MDC.get("traceId");

        Instant occurredAt = Instant.now();
        long startMs = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            long durationMs = System.currentTimeMillis() - startMs;

            auditLog.info(
                    "AUDIT | action={} resource={} outcome=SUCCESS method={} userId={} " +
                            "tenantId={} traceId={} durationMs={} occurredAt={} description={}",
                    action, resource, methodName,
                    userId != null ? userId : "anonymous",
                    tenantId != null ? tenantId : "none",
                    traceId != null ? traceId : "none",
                    durationMs, occurredAt, description);

            return result;

        } catch (Throwable ex) {
            long durationMs = System.currentTimeMillis() - startMs;

            auditLog.error(
                    "AUDIT | action={} resource={} outcome=FAILURE method={} userId={} " +
                            "tenantId={} traceId={} durationMs={} occurredAt={} error={} description={}",
                    action, resource, methodName,
                    userId != null ? userId : "anonymous",
                    tenantId != null ? tenantId : "none",
                    traceId != null ? traceId : "none",
                    durationMs, occurredAt, ex.getClass().getSimpleName() + ": " + ex.getMessage(),
                    description);

            throw ex; // always re-throw — AOP must not swallow exceptions
        }
    }
}
