package com._labor.fakecord.domain.events;

import java.util.UUID;

public record MusicUpdateEvent (
  UUID userId,
  String trackName,
  String artistName,
  String albumArtUrl,
  long durationMs,
  long progressMs,
  boolean isPlaying,
  long timestamp
) {}