package com._labor.fakecord.domain.request;

import java.time.Instant;
import java.util.UUID;

import com._labor.fakecord.domain.enums.ServerRestrictionType;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CreateServerRestrictionRequest {
  Long serverId;
  UUID operatorId;
  UUID targetId;
  ServerRestrictionType type;
  String reason;
  Instant expiredAt;
}