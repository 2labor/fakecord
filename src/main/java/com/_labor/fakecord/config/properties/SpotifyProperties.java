package com._labor.fakecord.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.connections.spotify")
public record SpotifyProperties(
  String clientIs,
  String secretKey,
  String redirectUrl,
  String scope
) {}
