package com._labor.fakecord.infrastructure.integration.spotify;

import java.time.Duration;
import java.util.UUID;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com._labor.fakecord.domain.dto.spotify.SpotifyCurrentlyPlayingResponse;
import com._labor.fakecord.domain.entity.UserConnection;
import com._labor.fakecord.domain.events.MusicUpdateEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SpotifyPollingProcessor {

  private final SpotifyClient client;
  private final SpotifyTokenService tokenService;
  private final RedisTemplate<String, String> redisTemplate;
  private final KafkaTemplate<String, MusicUpdateEvent> kafkaTemplate;
  private final ObjectMapper objectMapper;

  private static final long HEARTBEAT_INTERVAL_MS = 30_000;

  private static final String STATE_CACHE_KEY = "spotify:state:";
  private static final String TOPIC_MUSIC_UPDATES = "music-status-updates";

  @Async
  public void processConnection(UserConnection connection) {
    UUID userId = connection.getUser().getId();
    try {
      String token = tokenService.getValidAccessToken(connection);

      client.getCurrentTrack(token).ifPresentOrElse(
        response -> handlePayingState(userId, response),
        () -> handleEmptyState(userId)
      );
    } catch (Exception e) {
      log.error("Error during async processing for user {}: {}", userId, e.getMessage());
    }
  }

  private void handlePayingState(UUID userId, SpotifyCurrentlyPlayingResponse response) {
    String cacheKey = STATE_CACHE_KEY + userId;
    String statusKey = cacheKey + ":status";
    String lastUpdateKey = cacheKey + ":last_heartbeat";

    String currentTrackId = response.item().id();
    boolean isPlaying = response.isPlaying();

    String lastTrackId = redisTemplate.opsForValue().get(cacheKey + ":track_id");
    String lastStatus = redisTemplate.opsForValue().get(statusKey);
    String lastUpdateStr = redisTemplate.opsForValue().get(lastUpdateKey);

    long lastUpdateTime = (lastUpdateStr != null) ? Long.parseLong(lastUpdateStr) : 0;
    long currentTime = System.currentTimeMillis();

    boolean statusChanged = (null == lastStatus || !lastStatus.equals(String.valueOf(isPlaying)));
    boolean trackChanged = (null == lastTrackId || !lastTrackId.equals(currentTrackId));
    boolean isHeartbeatNeeded = isPlaying && (currentTime - lastUpdateTime > HEARTBEAT_INTERVAL_MS);

    if (!statusChanged && !trackChanged && !isHeartbeatNeeded) {
      return;
    }

    if (isHeartbeatNeeded && !trackChanged && !statusChanged) {
      log.debug("Heartbeat: Sending update for user {} (still playing {})", userId, response.item().name());
    } else {
      log.info("State updated for user {}: {} (Playing: {})", userId, response.item().name(), isPlaying);
    }

    MusicUpdateEvent event = new MusicUpdateEvent(
      userId,
      response.item().name(),
      response.item().artists().isEmpty() ? "Unknown Artist" : response.item().artists().get(0).name(),
      response.item().id(),
      response.item().album().images().isEmpty() ? null : response.item().album().images().get(0).imageUrl(),
      response.item().durationMs(),
      response.progressMs(),
      isPlaying,
      currentTime
    );

    try {
      String eventJson = objectMapper.writeValueAsString(event);

      kafkaTemplate.send(TOPIC_MUSIC_UPDATES, userId.toString(), event);
      redisTemplate.opsForValue().set(cacheKey, eventJson, Duration.ofMinutes(10));
      
      redisTemplate.opsForValue().set(cacheKey + ":track_id", currentTrackId, Duration.ofMinutes(10));
      redisTemplate.opsForValue().set(statusKey, String.valueOf(isPlaying), Duration.ofMinutes(10));
      redisTemplate.opsForValue().set(lastUpdateKey, String.valueOf(currentTime), Duration.ofMinutes(10));

    } catch (Exception e) {
      log.error("Failed to process heartbeat/update for user {}: {}", userId, e.getMessage());
    }
  }

  private void handleEmptyState(UUID userId) {
    String cacheKey = STATE_CACHE_KEY + userId;

    if (Boolean.TRUE.equals(redisTemplate.hasKey(cacheKey))) {
      log.info("User {} stopped listening to music", userId);
      kafkaTemplate.send(TOPIC_MUSIC_UPDATES, userId.toString(), new MusicUpdateEvent(
        userId, null , null, null, null, 0, 0, false, System.currentTimeMillis()
      ));
      redisTemplate.delete(cacheKey);
      redisTemplate.delete(cacheKey + ":track_id");
      redisTemplate.delete(cacheKey + ":status");
      redisTemplate.delete(cacheKey + ":last_heartbeat");
    }
  }
    
}
