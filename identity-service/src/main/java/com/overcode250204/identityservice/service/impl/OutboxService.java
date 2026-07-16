package com.overcode250204.identityservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.overcode250204.event.EventEnvelope;
import com.overcode250204.identityservice.entity.OutboxEvent;
import com.overcode250204.identityservice.repository.OutboxEventRepository;
import com.overcode250204.identityservice.service.IOutboxService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OutboxService implements IOutboxService {

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    @Value("${spring.application.name}")
    private String applicationName;



    @Override
    public void saveEvent(String topic, String eventType, UUID tenantId, String traceId, Map<String, Object> data) {
        String eventId = "evt_" + UUID.randomUUID();

        EventEnvelope envelope = new EventEnvelope(
                eventId,
                eventType,
                1,
                OffsetDateTime.now(),
                applicationName,
                traceId,
                tenantId.toString(),
                data);

        String payload = toJson(envelope);

        OutboxEvent event = new OutboxEvent(
                UUID.randomUUID(),
                eventId,
                eventType,
                topic,
                payload,
                "PENDING",
                0,
                OffsetDateTime.now(),
                null);

        outboxEventRepository.save(event);
    }

    private String toJson(EventEnvelope envelope) {
        try {
            return objectMapper.writeValueAsString(envelope);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Cannot serialize outbox event", ex);
        }
    }
}
