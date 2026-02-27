package com._labor.fakecord.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.connections.spotify")
public record SpotifyProperties(
  String clientId,
  String clientSecret,
  String redirectUri,
  String scope
) {}
