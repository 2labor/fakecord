package com._labor.fakecord.infrastructure.integration.spotify;

import java.util.UUID;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com._labor.fakecord.domain.enums.ConnectionProvider;
import com._labor.fakecord.infrastructure.outbox.domain.ConnectionCreatedPayload;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SpotifyPollingCoordinator {
  
  private final RedisTemplate<String, String> redisTemplate;
  private final ObjectMapper objectMapper;

  private static final String ACTIVE_POLLING_KEY = "spotify:active_polling";

  @KafkaListener(topics = "user-connection-events", groupId = "polling-group")
  public void handleConnectionEvent(String eventPayload) {
    try {
      ConnectionCreatedPayload payload = objectMapper.readValue(eventPayload, ConnectionCreatedPayload.class);

      UUID userId = payload.userId();

      if (payload.provider() == ConnectionProvider.SPOTIFY) {
        registerForPolling(userId);
      }
    } catch (Exception e) {
      log.error("Failed to parse connection event: {}", eventPayload, e);
    }
  }

  private void registerForPolling(UUID userId) {
    redisTemplate.opsForSet().add(ACTIVE_POLLING_KEY, userId.toString());
    log.info("User {} registered for active Spotify polling", userId);
  }
}