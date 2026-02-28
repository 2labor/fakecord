package com._labor.fakecord.services.impl;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com._labor.fakecord.domain.dto.spotify.SpotifyActivity;
import com._labor.fakecord.services.SpotifyService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SpotifyServiceImpl implements SpotifyService {

  private final RedisTemplate<String, String> redisTemplate;
  private final ObjectMapper objectMapper;

  private static final String STATE_CACHE_KEY = "spotify:state:";

  @Override
  public Optional<SpotifyActivity> getCachedActivity(UUID userId) {
    try {
      String json = redisTemplate.opsForValue().get(STATE_CACHE_KEY + userId);
      
      if (json == null) {
        return Optional.empty();
      }

      return Optional.of(objectMapper.readValue(json, SpotifyActivity.class));
    } catch (Exception e) {
      log.error("Error retrieving cached spotify activity for user {}: {}", userId, e.getMessage());
      return Optional.empty();
    }
  }
}
