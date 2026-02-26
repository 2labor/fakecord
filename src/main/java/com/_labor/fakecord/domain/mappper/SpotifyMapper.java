package com._labor.fakecord.domain.mappper;

import com._labor.fakecord.domain.dto.spotify.SpotifyActivity;
import com._labor.fakecord.domain.dto.spotify.SpotifyConnectionState;
import com._labor.fakecord.domain.dto.spotify.SpotifyCurrentlyPlayingResponse;
import com._labor.fakecord.domain.entity.UserConnection;

public interface SpotifyMapper {
  SpotifyConnectionState toStateDto(UserConnection connection);
  SpotifyActivity toActivityDto(SpotifyCurrentlyPlayingResponse response);
} 
