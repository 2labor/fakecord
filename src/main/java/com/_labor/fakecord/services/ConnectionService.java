package com._labor.fakecord.services;

import java.util.UUID;

import com._labor.fakecord.domain.enums.ConnectionProvider;


public interface ConnectionService {
  String getAuthorizationUrl(ConnectionProvider provider, UUID userId);
  void handleCallback(ConnectionProvider provider, UUID userId, String code, String state);
}
