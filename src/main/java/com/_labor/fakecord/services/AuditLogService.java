package com._labor.fakecord.services;

import com._labor.fakecord.domain.entity.AuditAction;
import com._labor.fakecord.domain.entity.User;
import com._labor.fakecord.domain.model.AuditMetadata;

import jakarta.servlet.http.HttpServletRequest;

public interface AuditLogService {
  void log(User user, AuditAction auditAction, HttpServletRequest request, AuditMetadata metadata);
}
