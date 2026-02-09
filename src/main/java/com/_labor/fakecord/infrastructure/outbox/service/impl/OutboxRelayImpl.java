package com._labor.fakecord.infrastructure.outbox.service.impl;

import java.time.Instant;
import java.util.List;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com._labor.fakecord.infrastructure.outbox.domain.EventStatus;
import com._labor.fakecord.infrastructure.outbox.domain.OutboxEvent;
import com._labor.fakecord.infrastructure.outbox.repository.OutboxRepository;
import com._labor.fakecord.infrastructure.outbox.service.OutboxRelay;



import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class OutboxRelayImpl implements OutboxRelay {

  private final OutboxRepository repository;
  private final KafkaTemplate<String, String> kafkaTemplate;

  private static final String TOPIC = "user-registration-events";

  public OutboxRelayImpl(
    OutboxRepository repository,
    KafkaTemplate<String, String> kafkaTemplate
  ){
    this.repository = repository;
    this.kafkaTemplate = kafkaTemplate;
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
      relayToKafka(event);
      finalizeEvent(event);
    } catch (Exception e) {
      log.error("Failed to process outbox event {}: {}", event.getId(), e.getMessage());
    }
  }

  private void relayToKafka(OutboxEvent event) {
    kafkaTemplate.send(TOPIC, event.getAggregateId().toString(), event.getPayload())
      .whenComplete((result, ex) -> {
        if (null != ex) {
          log.error("Unable to send message=[{}] due to : {}", event.getPayload(), ex.getMessage());
        } else {
          log.debug("Sent message=[{}] with offset=[{}]", event.getPayload(), result.getRecordMetadata().offset());
        }
      });
  } 

  private void finalizeEvent(OutboxEvent event) {
    event.setStatus(EventStatus.PROCESS);
    event.setProcessAt(Instant.now());
    repository.save(event);
  }
}
