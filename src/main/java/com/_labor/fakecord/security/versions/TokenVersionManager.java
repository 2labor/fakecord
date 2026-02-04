package com._labor.fakecord.security.versions;

import java.util.UUID;

public interface TokenVersionManager {
  int getCurrentVersion(UUID userId);
  void evictVersion(UUID userId);
  void updateRedisCache(UUID userId, int version);
}
