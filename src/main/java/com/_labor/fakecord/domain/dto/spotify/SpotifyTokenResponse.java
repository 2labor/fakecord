package com._labor.fakecord.domain.dto.spotify;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SpotifyTokenResponse(
  @JsonProperty("access_token") String accessToken,
  @JsonProperty("refresh_token") String refreshToken,
  @JsonProperty("expires_in") Integer expiresIn,
  @JsonProperty("token_type") String tokenType,
  @JsonProperty("scope") String scope
) {}