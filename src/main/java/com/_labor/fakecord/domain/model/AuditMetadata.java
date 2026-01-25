package com._labor.fakecord.domain.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.PROPERTY,
  property = "type"
)
@JsonSubTypes({
  @JsonSubTypes.Type(value = BackupCodeMetadata.class, name = "backup_code"),
  @JsonSubTypes.Type(value = PasswordChangeMetadata.class, name = "password_change")
})
public interface AuditMetadata {}
