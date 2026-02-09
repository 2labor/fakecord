package com._labor.fakecord.infrastructure.outbox.service;

public interface OutboxRelay {
  void processNextBatch();
}
