package com._labor.fakecord.infrastructure.outbox.service.impl;

import java.time.Instant;
import java.util.List;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com._labor.fakecord.infrastructure.outbox.domain.CacheEvictEvent;
import com._labor.fakecord.infrastructure.outbox.domain.EventStatus;
import com._labor.fakecord.infrastructure.outbox.domain.OutboxEvent;
import com._labor.fakecord.infrastructure.outbox.repository.OutboxRepository;
import com._labor.fakecord.infrastructure.outbox.service.EventPublisher;
import com._labor.fakecord.infrastructure.outbox.service.OutboxRelay;



import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class OutboxRelayImpl implements OutboxRelay {

  private final OutboxRepository repository;
  private final KafkaTemplate<String, String> kafkaTemplate;
  private final EventPublisher publisher;

  private static final String KAFKA_TOPIC = "user-registration-events";

  public OutboxRelayImpl(
    OutboxRepository repository,
    KafkaTemplate<String, String> kafkaTemplate,
    EventPublisher publisher
  ){
    this.repository = repository;
    this.kafkaTemplate = kafkaTemplate;
    this.publisher = publisher;
  }

  @Override
  @Transactional
  public void processNextBatch() {
    log.debug("Fetching pending outbox events for processing...");

    List<OutboxEvent> events = repository.findTopPending();

    if (events.isEmpty()) {
      log.trace("No pending outbox events found.");
      return;
    }

    log.info("Relaying batch of {} events to message broker", events.size());

    events.forEach(this::processEvent);
  }
  

  private void processEvent(OutboxEvent event) {
    try {
      switch (event.getType()) {
        case USER_PROFILE_UPDATED -> handleCacheEviction(event);
                case USER_REGISTERED, USER_EMAIL_VERIFIED, USER_DELETED -> handleBusinessEvent(event);
        default -> log.warn("Unknown event type: {}", event.getType());
      }
    } catch (Exception e) {
      log.error("Failed to process outbox event with type {}: {}, {}", event.getId(), event.getType(), e.getMessage());
    }
  }

  private void relayToKafka(OutboxEvent event) {
    kafkaTemplate.send(KAFKA_TOPIC, event.getAggregateId().toString(), event.getPayload())
      .whenComplete((result, ex) -> {
        if (null != ex) {
          log.error("Unable to send message=[{}] due to : {}", event.getPayload(), ex.getMessage());
        } else {
          log.debug("Sent message=[{}] with offset=[{}]", event.getPayload(), result.getRecordMetadata().offset());
        }
      });
  } 

  private void handleCacheEviction(OutboxEvent event) {
    publisher.publish(
      new CacheEvictEvent(
        event.getAggregateId(),
        "user_profiles",
        System.currentTimeMillis()
      ));
    finalizeEvent(event);
  }

  private void handleBusinessEvent(OutboxEvent event) {
    try {
      kafkaTemplate.send(KAFKA_TOPIC, event.getAggregateId().toString(), event.getPayload())
        .get();
      finalizeEvent(event);
    } catch (Exception e) {
      throw new RuntimeException("Kafka send failed", e);
    }
  }

  private void finalizeEvent(OutboxEvent event) {
    event.setStatus(EventStatus.PROCESS);
    event.setProcessAt(Instant.now());
    repository.save(event);
  }
}
