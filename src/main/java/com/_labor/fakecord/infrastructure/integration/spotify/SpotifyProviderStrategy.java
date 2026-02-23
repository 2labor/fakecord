package com._labor.fakecord.infrastructure.integration.spotify;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com._labor.fakecord.config.properties.SpotifyProperties;
import com._labor.fakecord.domain.dto.SpotifyTokenResponse;
import com._labor.fakecord.domain.dto.SpotifyUserProfile;
import com._labor.fakecord.domain.entity.User;
import com._labor.fakecord.domain.entity.UserConnection;
import com._labor.fakecord.domain.enums.ConnectionProvider;
import com._labor.fakecord.domain.strategy.ConnectionProviderStrategy;
import com._labor.fakecord.repository.UserConnectionRepository;
import com._labor.fakecord.repository.UserRepository;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SpotifyProviderStrategy implements ConnectionProviderStrategy {

  private final SpotifyProperties spotifyProperties;
  private final RedisTemplate<String, String> redisTemplate;
  private final UserRepository userRepository;
  private final UserConnectionRepository repository;
  private final SpotifyClient spotifyClient;

  private final String STATE_KEY_PREFIX = "oauth_state:";

  @Override
  public ConnectionProvider getProvider() {
    return ConnectionProvider.SPOTIFY;
  }

  @Override
  public String buildAuthorizationUrl(UUID userId) {
    String state = UUID.randomUUID().toString();

    String redisKey = STATE_KEY_PREFIX + userId;
    redisTemplate.opsForValue().set(redisKey, state, Duration.ofMinutes(5));

    log.info("Creating auth URL for user: {}. State saved to Redis.", userId);

    return UriComponentsBuilder.fromUriString("https://accounts.spotify.com/authorize")
    .queryParam("client_id", spotifyProperties.clientId())
      .queryParam("response_type", "code")
      .queryParam("redirect_uri", spotifyProperties.redirectUri())
      .queryParam("scope", spotifyProperties.scope())
      .queryParam("state", state)
      .build()
      .toUriString();
  }

  @Override
  public void handleCallback(UUID userId, String code, String state) {
    log.info("Starting callback handling for user: {}", userId);

    validateState(userId, state);

    SpotifyTokenResponse response = spotifyClient.fetchTokens(code);
    SpotifyUserProfile profile = spotifyClient.fetchUserProfile(response.accessToken());

    saveUserConnection(userId, response, profile);

    log.info("Successfully connected Spotify for user: {}", userId);
  }

  private void validateState(UUID userId, String state) {
    String redisKey = STATE_KEY_PREFIX + userId;
    String savedState = redisTemplate.opsForValue().get(redisKey);
    
    if (savedState == null || !savedState.equals(state)) {
        log.error("CSRF attack detected or session expired for user: {}", userId);
        throw new RuntimeException("Invalid CSRF state");
    }
    
    redisTemplate.delete(redisKey);
    log.debug("State validated and cleaned up for user: {}", userId);
  }

  private void saveUserConnection(UUID userId, SpotifyTokenResponse tokens, SpotifyUserProfile profile) {
    User user = userRepository.findById(userId)
      .orElseThrow(() -> new RuntimeException("User not found"));

    UUID existingConnections = repository.findByUserAndProvider(user, ConnectionProvider.SPOTIFY)
      .map(UserConnection::getId)
      .orElse(null);

    UserConnection connection = UserConnection.builder()
      .id(existingConnections)
      .user(user)
      .provider(ConnectionProvider.SPOTIFY)
      .accessToken(tokens.accessToken())
      .refreshToken(tokens.refreshToken())
      .expiresAt(Instant.now().plusSeconds(tokens.expiresIn()))
      .externalId(profile.id())
      .externalName(profile.displayName())
      .showOnProfile(true)
      .build();

    repository.save(connection);

    log.info("Connection {} for user {} saved successfully via Builder", existingConnections == null ? "created" : "updated", userId);
  }
}
