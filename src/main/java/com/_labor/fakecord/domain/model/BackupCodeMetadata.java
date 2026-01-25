package com._labor.fakecord.domain.model;

import java.util.UUID;

public record BackupCodeMetadata(
  String prefix,
  UUID codeId,
  int attemptNumber
) implements AuditMetadata {} 
