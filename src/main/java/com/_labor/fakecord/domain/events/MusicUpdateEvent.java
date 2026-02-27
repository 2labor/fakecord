package com._labor.fakecord.domain.events;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MusicUpdateEvent (
  UUID userId,
  String trackName,
  String artistName,
  @JsonProperty("albumArtUrl")
  String albumArtUrl,
  long durationMs,
  long progressMs,
  boolean isPlaying,
  long timestamp
) {}