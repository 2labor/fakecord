package com._labor.fakecord.domain.entity;

public enum AuditAction {
  BACKUP_CODE_GENERATED,
  BACKUP_CODE_USED_SUCCESS,
  BACKUP_CODE_USED_FAILED,
  MFA_ENABLED,
  MFA_DISABLED,
  FAILED_LOGIN_ATTEMPT
}
