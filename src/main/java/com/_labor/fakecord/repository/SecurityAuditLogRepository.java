package com._labor.fakecord.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com._labor.fakecord.domain.entity.SecurityAuditLog;

public interface SecurityAuditLogRepository extends JpaRepository<SecurityAuditLog, UUID> {
  List<SecurityAuditLog> findTop10ByUserIdOrderByCreatedAtDesc(UUID userId);
}
