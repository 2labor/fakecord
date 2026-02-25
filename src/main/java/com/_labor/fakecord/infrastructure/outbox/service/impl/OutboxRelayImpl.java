package com._labor.fakecord.infrastructure.outbox.service.impl;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import com._labor.fakecord.infrastructure.outbox.domain.EventStatus;
import com._labor.fakecord.infrastructure.outbox.domain.OutboxEvent;
import com._labor.fakecord.infrastructure.outbox.repository.OutboxRepository;
import com._labor.fakecord.infrastructure.outbox.service.OutboxHandler;
import com._labor.fakecord.infrastructure.outbox.service.OutboxRelay;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
@RequiredArgsConstructor
public class OutboxRelayImpl implements OutboxRelay {
  
  private final OutboxRepository repository;
  private final List<OutboxHandler> handlers;
  
  @Override
  @Transactional
  public void processNextBatch() {
    List<OutboxEvent> events = repository.findTopPending();

    if (events.isEmpty()) {
      return;
    }

    log.info("Relaying batch of {} events via handlers", events.size());

    for (OutboxEvent event : events) {
      processEvent(event);
    }
  }

  private void processEvent(OutboxEvent event) {
    handlers.stream()
      .filter(handler -> handler.supports(event.getType()))
      .findFirst()
      .ifPresentOrElse(
        handler -> {
          try {
            handler.handle(event);
            finalizeEvent(event);
          } catch (Exception e) {
            log.error("Failed to process event {} with handler {}: {}", event.getId(), handler.getClass().getSimpleName(), e.getMessage());
          }
        }, 
        () -> log.warn("No suitable handler found for event type: {}", event.getType())
      );
  }

  private void finalizeEvent(OutboxEvent event) {
    event.setStatus(EventStatus.PROCESS);
    event.setProcessAt(Instant.now());
    repository.save(event);
  }
}
