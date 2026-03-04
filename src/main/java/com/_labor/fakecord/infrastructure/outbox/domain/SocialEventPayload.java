package com._labor.fakecord.infrastructure.outbox.domain;

import java.util.UUID;

public record SocialEventPayload(
  UUID actorId,
  UUID targetId,
  String metadata
) {}