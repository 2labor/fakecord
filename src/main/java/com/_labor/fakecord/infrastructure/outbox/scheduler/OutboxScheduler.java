package com._labor.fakecord.infrastructure.outbox.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com._labor.fakecord.infrastructure.outbox.service.OutboxRelay;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class OutboxScheduler {
  
  private final OutboxRelay relay;

  public OutboxScheduler(OutboxRelay relay) {
    this.relay = relay;
  }


  @Scheduled(fixedDelayString = "${app.outbox.scheduler.interval-ms:5000}")
  public void scheduleRelay() {
    log.trace("Outbox scheduler triggered");
    try {
      relay.processNextBatch();
    } catch (Exception e) {
      log.error("Error occurred during outbox scheduled task", e);
    }
  }
}
