package com._labor.fakecord.domain.dto.spotify;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SpotifyUserProfile( 
  String id,
  @JsonProperty("display_name") 
  String displayName
) {}