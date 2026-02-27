package com._labor.fakecord.domain.dto.spotify;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record SpotifyCurrentlyPlayingResponse(
  @JsonProperty("is_playing") 
  boolean isPlaying,
  @JsonProperty("progress_ms")
  long progressMs,
  Item item
) {
  public record Item(
    String id,
    String name, 
    @JsonProperty("duration_ms")
    long durationMs,  
    List<Artist> artists,
    Album album
  ) {}

  public record Artist(String name) {}
  public record Album(List<CoverImage> images) {}
  public record CoverImage(
    @JsonProperty("url") 
    String imageUrl
  ) {}
}