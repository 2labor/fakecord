package com._labor.fakecord.infrastructure.integration;

import com._labor.fakecord.domain.entity.UserConnection;
import com._labor.fakecord.domain.enums.ConnectionProvider;

public interface OAuthTokenProvider {
  String getValidAccessToken(UserConnection connection);
  boolean supports(ConnectionProvider provider);
}
