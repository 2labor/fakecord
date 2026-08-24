package com._labor.fakecord.infrastructure.outbox.domain.payload;

import java.time.Instant;
import java.util.UUID;

import com._labor.fakecord.domain.enums.ServerRestrictionType;

public record ServerRestrictionAppliedPayload(
  Long restrictionId,
  Long serverId,
  UUID targetId,
  UUID operatorId,
  ServerRestrictionType type,
  String reason,
  Instant expiredAt
) {}