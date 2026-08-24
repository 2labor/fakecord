package com._labor.fakecord.infrastructure.outbox.domain.payload;

import java.util.UUID;

import com._labor.fakecord.domain.enums.ServerRestrictionType;

public record ServerRestrictionDeactivatedPayload(
  Long restrictionId,
  Long serverId,
  UUID targetId,
  ServerRestrictionType type
) {}
