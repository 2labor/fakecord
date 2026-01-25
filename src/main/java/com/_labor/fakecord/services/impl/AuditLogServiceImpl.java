package com._labor.fakecord.services.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com._labor.fakecord.domain.entity.AuditAction;
import com._labor.fakecord.domain.entity.SecurityAuditLog;
import com._labor.fakecord.domain.entity.User;
import com._labor.fakecord.domain.model.AuditMetadata;
import com._labor.fakecord.repository.SecurityAuditLogRepository;
import com._labor.fakecord.services.AuditLogService;
import com._labor.fakecord.utils.RequestUtil;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class AuditLogServiceImpl implements AuditLogService{

  private final SecurityAuditLogRepository repository;

  public AuditLogServiceImpl(SecurityAuditLogRepository repository) {
    this.repository = repository;
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void log(User user, AuditAction auditAction, HttpServletRequest request, AuditMetadata metadata) {
    SecurityAuditLog log = SecurityAuditLog.builder()
      .user(user)
      .ipAddress(RequestUtil.getClientIp(request))
      .agent(RequestUtil.getClientAgent(request))
      .deviceType(RequestUtil.getDeviceType(request))
      .os(RequestUtil.getClientOperationSystem(request))
      .metadata(metadata)
      .build();
    
      repository.save(log);
  }
  
}
