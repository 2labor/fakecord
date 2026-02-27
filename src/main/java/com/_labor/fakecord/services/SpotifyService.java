package com._labor.fakecord.services;

import java.util.Optional;
import java.util.UUID;

import com._labor.fakecord.domain.dto.spotify.SpotifyActivity;

public interface SpotifyService {
  Optional<SpotifyActivity> getCachedActivity(UUID userId);
}