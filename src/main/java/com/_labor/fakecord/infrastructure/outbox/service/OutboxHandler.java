package com._labor.fakecord.infrastructure.outbox.service;

import com._labor.fakecord.infrastructure.outbox.domain.OutboxEvent;
import com._labor.fakecord.infrastructure.outbox.domain.OutboxEventType;

public interface OutboxHandler {
  boolean supports(OutboxEventType type);
  void handle(OutboxEvent event);
}
