package com._labor.fakecord.domain.dto;

import java.util.UUID;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import lombok.Builder;

@Builder
public record ServerRestrictionResponse(
  @JsonSerialize(using = ToStringSerializer.class)
  Long id,
  @JsonSerialize(using = ToStringSerializer.class)
  Long serverId,
  UUID operatorId,
  UUID targetId,
  String type,
  String reason,
  boolean active,
  @JsonSerialize(using = ToStringSerializer.class)
  Long expiredAt,
  @JsonSerialize(using = ToStringSerializer.class)
  Long createdAt
) {}
