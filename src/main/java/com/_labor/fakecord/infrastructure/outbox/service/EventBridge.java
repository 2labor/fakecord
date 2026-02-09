package com._labor.fakecord.infrastructure.outbox.service;

public interface EventBridge {
  void process(String message);
}
