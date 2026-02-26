package com._labor.fakecord.domain.dto.spotify;

import java.util.List;

public record SpotifyCurrentlyPlayingResponse(
  boolean isPlaying,
  long progressMs,
  Item item
) {
  public record Item(
    String id,
    String name, 
    long durationMs,
    List<Artist> artists,
    Album album
  ) {}
  public record Artist(String name) {}
  public record Album(List<CoverImage> images) {}
  public record CoverImage(String imageUrl) {}
}