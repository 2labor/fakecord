package com._labor.fakecord.services;

import java.util.UUID;

public interface UserStatusService {
  void setOnline(UUID userId);
  void setOffline(UUID userId);
  boolean isOnline(UUID userId);
}
