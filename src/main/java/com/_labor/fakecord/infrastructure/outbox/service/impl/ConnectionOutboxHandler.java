package com._labor.fakecord.infrastructure.outbox.service.impl;

import java.util.Set;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com._labor.fakecord.infrastructure.outbox.domain.OutboxEvent;
import com._labor.fakecord.infrastructure.outbox.domain.OutboxEventType;
import com._labor.fakecord.infrastructure.outbox.service.OutboxHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class ConnectionOutboxHandler implements OutboxHandler{

  private final KafkaTemplate<String, String> kafkaTemplate;
  private static final String TOPIC = "user-connection-events";

  private static final Set<OutboxEventType> SUPPORTED_TYPES = Set.of(
    OutboxEventType.USER_CONNECTION_CREATED,
    OutboxEventType.USER_CONNECTION_DELETED
  );

  @Override
  public boolean supports(OutboxEventType type) {
    return SUPPORTED_TYPES.contains(type);
  }

  @Override
  public void handle(OutboxEvent event) {
    try {
      kafkaTemplate.send(TOPIC, event.getAggregateId().toString(), event.getPayload().toString());
      log.info("Connection event {} for user {} relayed to Kafka", event.getType(), event.getAggregateId());
    } catch (Exception e) {
      throw new RuntimeException("Failed to relay connection event: " + event.getType(), e);
    } 
  }
}
