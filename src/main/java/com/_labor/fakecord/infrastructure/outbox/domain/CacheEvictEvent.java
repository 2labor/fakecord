package com._labor.fakecord.infrastructure.outbox.domain;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

public record CacheEvictEvent(
  UUID aggregateId,
  String cacheName,
  long timeStamp
) implements Serializable {}
