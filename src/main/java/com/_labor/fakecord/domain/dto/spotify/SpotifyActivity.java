package com._labor.fakecord.domain.dto.spotify;

public record SpotifyActivity(
  String trackName,
  String artistName,
  String albumUrl,
  long durationMs,
  long progressMs,
  boolean isPlaying,
  long updatedAt
) {}