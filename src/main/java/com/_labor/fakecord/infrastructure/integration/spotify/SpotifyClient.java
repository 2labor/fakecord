package com._labor.fakecord.infrastructure.integration.spotify;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com._labor.fakecord.config.properties.SpotifyProperties;
import com._labor.fakecord.domain.dto.SpotifyTokenResponse;
import com._labor.fakecord.domain.dto.SpotifyUserProfile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class SpotifyClient {
  
  private final WebClient webClient;
  private final SpotifyProperties properties;

  public SpotifyTokenResponse fetchTokens(String code) {
    return webClient.post()
      .uri("https://accounts.spotify.com/api/token")
      .contentType(MediaType.APPLICATION_FORM_URLENCODED)
      .body(BodyInserters.fromFormData("grant_type", "authorization_code")
        .with("code", code)
        .with("redirect_uri", properties.redirectUri())
        .with("client_id", properties.clientId())
        .with("client_secret", properties.secretKey()))
      .retrieve()
      .bodyToMono(SpotifyTokenResponse.class)
      .block();
  }

  public SpotifyUserProfile fetchUserProfile(String accessToken) {
    log.debug("Fetching Spotify user profile with access token");
    return webClient.get()
      .uri("https://api.spotify.com/v1/me")
      .header("Authorization", "Bearer " + accessToken)
      .retrieve()
      .bodyToMono(SpotifyUserProfile.class)
      .block();
  }

  public SpotifyTokenResponse refreshToken(String refreshToken) {
    log.debug("Refreshing Spotify access token using refresh_token");

    try {
      return webClient.post()
        .uri("https://accounts.spotify.com/api/token")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .body(BodyInserters.fromFormData("grant_type", "refresh_token")
          .with("refresh_token", refreshToken)
          .with("client_id", properties.clientId())
          .with("client_secret", properties.secretKey()))
        .retrieve()
        .bodyToMono(SpotifyTokenResponse.class)
        .block();
    } catch (Exception e) {
      log.error("Failed to refresh Spotify token: {}", e.getMessage());
      throw new RuntimeException("Spotify token refresh failed", e);
    }
  }
}
