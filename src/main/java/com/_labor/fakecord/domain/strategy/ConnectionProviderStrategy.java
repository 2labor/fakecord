package com._labor.fakecord.domain.strategy;

import java.util.UUID;

import com._labor.fakecord.domain.enums.ConnectionProvider;

public interface ConnectionProviderStrategy {
  ConnectionProvider getProvider();
  String buildAuthorizationUrl(UUID userId);
  void handleCallBack(UUID userId, String code, String state);
}
