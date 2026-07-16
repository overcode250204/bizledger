package com.overcode250204.identityservice.service.impl;

import com.overcode250204.identityservice.entity.OutboxEvent;
import com.overcode250204.identityservice.repository.OutboxEventRepository;
import com.overcode250204.identityservice.service.IOutboxPublisherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxPublisherService implements IOutboxPublisherService {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedRate = 2000)
    @Transactional
    @Override
    public void publishEvents() {
        List<OutboxEvent> events = outboxEventRepository.findPending();
        if (events.isEmpty()) {
            return;
        }

        log.debug("[Outbox] Polled {} pending events to publish", events.size());

        for (OutboxEvent event : events) {
            try {
                // Publish to Kafka message broker
                kafkaTemplate.send(event.getTopic(), event.getEventId(), event.getPayload()).get();

                event.setStatus("PUBLISHED");
                event.setPublishedAt(OffsetDateTime.now());
                outboxEventRepository.save(event);

                log.info("[Outbox] Published identity event successfully | eventId={} type={}",
                        event.getEventId(), event.getEventType());

            } catch (Exception e) {
                log.error("[Outbox] Failed to publish event | eventId={}", event.getEventId(), e);
                event.setRetryCount(event.getRetryCount() + 1);
                if (event.getRetryCount() >= 5) {
                    event.setStatus("FAILED");
                }
                outboxEventRepository.save(event);
            }
        }
    }
}
