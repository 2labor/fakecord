package com._labor.fakecord.domain.dto.spotify;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SpotifyActivity(
  String trackName,
  String artistName,
  @JsonProperty("albumArtUrl")
  String albumUrl,
  long durationMs,
  long progressMs,
  boolean isPlaying,
  @JsonProperty("timestamp") 
  long updatedAt
) {}