package com._labor.fakecord.domain.dto.spotify;

public record SpotifyConnectionState(
  boolean isConnected,
  String spotifyUserName
) {}