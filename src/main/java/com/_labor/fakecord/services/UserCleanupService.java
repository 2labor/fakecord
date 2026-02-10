package com._labor.fakecord.services;

import java.util.UUID;

public interface UserCleanupService {
  void scrubUnverifiedUser(UUID userId);
}
