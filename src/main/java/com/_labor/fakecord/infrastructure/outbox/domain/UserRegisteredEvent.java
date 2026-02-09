package com._labor.fakecord.infrastructure.outbox.domain;

import java.util.UUID;

public record UserRegisteredEvent(
  UUID userId,
  String email,
  String username
) {}
