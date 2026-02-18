package com._labor.fakecord.infrastructure.outbox.service;

import com._labor.fakecord.infrastructure.outbox.domain.CacheEvictEvent;

public interface EventPublisher {
  void publish(CacheEvictEvent event);
}
