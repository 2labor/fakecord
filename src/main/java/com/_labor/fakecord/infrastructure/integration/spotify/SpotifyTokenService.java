package com._labor.fakecord.infrastructure.integration.spotify;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Service;

import com._labor.fakecord.domain.dto.spotify.SpotifyTokenResponse;
import com._labor.fakecord.domain.entity.UserConnection;
import com._labor.fakecord.domain.enums.ConnectionProvider;
import com._labor.fakecord.infrastructure.integration.OAuthTokenProvider;
import com._labor.fakecord.repository.UserConnectionRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SpotifyTokenService implements OAuthTokenProvider {

  private final SpotifyClient client;
  private final UserConnectionRepository repository;

  @Override
  @Transactional
  public String getValidAccessToken(UserConnection connection) {
    if (isTokenExpired(connection)) {
      log.info("Token for user {} is expiring soon. Refreshing...", connection.getUser().getId());
      return refreshAndSaveToken(connection);
    }

    return connection.getAccessToken();
  }

  @Override
  public boolean supports(ConnectionProvider provider) {
    return provider == ConnectionProvider.SPOTIFY;
  }
  
  private boolean isTokenExpired(UserConnection connection) {
    return connection.getExpiresAt()
      .minus(5, ChronoUnit.MINUTES)
      .isBefore(Instant.now());
  }

  private String refreshAndSaveToken(UserConnection connection) {
    SpotifyTokenResponse response = client.refreshToken(connection.getRefreshToken());

    connection.setAccessToken(response.accessToken());
    if (null != response.refreshToken()) {
      connection.setRefreshToken(response.refreshToken());
    }
    connection.setExpiresAt(Instant.now().plusSeconds(response.expiresIn()));
    repository.save(connection);
    log.debug("Token successfully refreshed and persisted for user {}", connection.getUser().getId());

    return connection.getAccessToken();
  }
}
