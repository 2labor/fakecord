package com._labor.fakecord.domain.strategy;

import java.util.UUID;

import com._labor.fakecord.domain.dto.ConnectionStatusDto;
import com._labor.fakecord.domain.enums.ConnectionProvider;

public interface ConnectionProviderStrategy {
  ConnectionProvider getProvider();
  String buildAuthorizationUrl(UUID userId);
  void handleCallback(UUID userId, String code, String state);
  ConnectionStatusDto getStatus(UUID userId);
  void toggleVisibility(UUID userId);
  void disconnect(UUID userId);
}
