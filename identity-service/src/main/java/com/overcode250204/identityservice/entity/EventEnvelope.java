package com.overcode250204.identityservice.entity;

import java.time.OffsetDateTime;
import java.util.Map;

public record EventEnvelope(
        String eventId,
        String eventType,
        int eventVersion,
        OffsetDateTime occurredAt,
        String source,
        String traceId,
        String tenantId,
        Map<String, Object> data
) {
}
