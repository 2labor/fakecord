package com._labor.fakecord.infrastructure.outbox;

import java.util.UUID;

public interface OutboxService {
  void publish(UUID aggregateId, OutboxEventType type, Object payload);
}
