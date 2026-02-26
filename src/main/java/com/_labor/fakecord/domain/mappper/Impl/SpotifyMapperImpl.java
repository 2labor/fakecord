package com._labor.fakecord.domain.mappper.Impl;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com._labor.fakecord.domain.dto.spotify.SpotifyActivity;
import com._labor.fakecord.domain.dto.spotify.SpotifyConnectionState;
import com._labor.fakecord.domain.dto.spotify.SpotifyCurrentlyPlayingResponse;
import com._labor.fakecord.domain.entity.UserConnection;
import com._labor.fakecord.domain.mappper.SpotifyMapper;

@Component
public class SpotifyMapperImpl implements SpotifyMapper {

  @Override
  public SpotifyConnectionState toStateDto(UserConnection connection) {
    if (null == connection) return new SpotifyConnectionState(false, null);
    return new SpotifyConnectionState(true, connection.getExternalName());
  }

  @Override
  public SpotifyActivity toActivityDto(SpotifyCurrentlyPlayingResponse response) {
    if (response == null || response.item() == null) return null;

    var item = response.item();
    
    String artists = item.artists().stream()
      .map(SpotifyCurrentlyPlayingResponse.Artist::name)
      .collect(Collectors.joining(", "));
    
    String artUrl = (null != item.album() && !item.album().images().isEmpty()) 
      ? item.album().images().get(0).imageUrl() 
      : null;

    return new SpotifyActivity (
      item.name(),
      artists,
      artUrl,
      item.durationMs(),
      response.progressMs(),
      response.isPlaying(),
      System.currentTimeMillis()
    );
  } 
}
