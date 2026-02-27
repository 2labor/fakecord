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

    String currentTrack = response.item().id();
    boolean isPlaying = response.isPlaying();

    String lastTrackId = redisTemplate.opsForValue().get(cacheKey);
    String lastStatus = redisTemplate.opsForValue().get(statusKey);
  
    boolean statusChanged = (null == lastStatus || !lastStatus.equals(String.valueOf(isPlaying)));
    boolean trackChanged = (null == lastTrackId || !lastTrackId.equals(currentTrack));

    if (statusChanged || trackChanged) {
      log.info("State updated for user {}: {} (Playing: {})", userId, response.item().name(), isPlaying);

      MusicUpdateEvent event = new MusicUpdateEvent(
        userId, 
        response.item().name(),
        response.item().artists().get(0).name(),
        response.item().album().images().get(0).imageUrl(),
        response.item().durationMs(),
        response.progressMs(),
        response.isPlaying(),
        System.currentTimeMillis()
      );

      kafkaTemplate.send(TOPIC_MUSIC_UPDATES, userId.toString(), event);
    
      redisTemplate.opsForValue().set(cacheKey, currentTrack, Duration.ofMinutes(10));
      redisTemplate.opsForValue().set(statusKey, String.valueOf(isPlaying), Duration.ofMinutes(10));
    }
  
  }

  private void handleEmptyState(UUID userId) {
    String cacheKey = STATE_CACHE_KEY + userId;

    if (Boolean.TRUE.equals(redisTemplate.hasKey(cacheKey))) {
      log.info("User {} stopped listening to music", userId);
      kafkaTemplate.send(TOPIC_MUSIC_UPDATES, userId.toString(), new MusicUpdateEvent(
        userId, null, null, null, 0, 0, false, System.currentTimeMillis()
      ));
      redisTemplate.delete(cacheKey);
    }
  }
    
}
