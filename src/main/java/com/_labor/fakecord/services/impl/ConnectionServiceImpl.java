package com._labor.fakecord.services.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com._labor.fakecord.domain.enums.ConnectionProvider;
import com._labor.fakecord.domain.strategy.ConnectionProviderStrategy;
import com._labor.fakecord.services.ConnectionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConnectionServiceImpl implements ConnectionService {

  private final List<ConnectionProviderStrategy> strategies;

  @Override
  public String getAuthorizationUrl(ConnectionProvider provider, UUID userId) {
    return findStrategy(provider).buildAuthorizationUrl(userId);
  }

  @Override
  public void handleCallback(ConnectionProvider provider, UUID userId, String code, String state) {
    findStrategy(provider).handleCallback(userId, code, state);
  }
  
  private ConnectionProviderStrategy findStrategy(ConnectionProvider provider) {
    return strategies.stream()
      .filter(s -> s.getProvider() == provider)
      .findFirst()
      .orElseThrow(() -> new RuntimeException("Provider not supported: " + provider));
  }
}
