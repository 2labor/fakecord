package com._labor.fakecord.infrastructure.outbox.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com._labor.fakecord.infrastructure.outbox.domain.OutboxEvent;

public interface OutboxRepository extends JpaRepository<OutboxEvent, UUID> {
  @Query(value = """
    SELECT * FROM outbox_events 
    WHERE status = 'PENDING' 
    ORDER BY create_at ASC 
    LIMIT 50
    FOR UPDATE SKIP LOCKED
    """, nativeQuery = true)
  List<OutboxEvent> findTopPending();
}