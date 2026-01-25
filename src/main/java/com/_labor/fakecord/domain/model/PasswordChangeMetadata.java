package com._labor.fakecord.domain.model;

public record PasswordChangeMetadata(
  String reason
) implements AuditMetadata {}
