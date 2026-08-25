package com._labor.fakecord.infrastructure.cache.Dto;

import java.time.Instant;
import java.util.UUID;

public record UserTimeoutCacheDto(
  Long serverId,
  UUID targetId,
  String reason,
  Long expiredAt
) {}