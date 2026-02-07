package com._labor.fakecord.infrastructure.outbox;

import java.util.UUID;

public record UserRegisteredEvent(
  UUID userId,
  String email,
  String username
) {}
