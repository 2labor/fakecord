package com._labor.fakecord.infrastructure.integration.spotify;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com._labor.fakecord.domain.entity.UserConnection;
import com._labor.fakecord.domain.enums.ConnectionProvider;
import com._labor.fakecord.repository.UserConnectionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class SpotifyPollingWorker {
  private final RedisTemplate<String, String> redisTemplate;
  private final UserConnectionRepository repository;
  private final SpotifyTokenService tokenService;
  private final SpotifyClient client;
  // private final KafkaTemplate<String, Object> kafkaTemplate;

  private static final String ACTIVE_POLLING_KEY = "spotify:active_polling";

  @Scheduled(fixedDelay = 15000)
  public void pooling() {
    Set<String> userIds = redisTemplate.opsForSet().members(ACTIVE_POLLING_KEY);
    if (null == userIds || userIds.isEmpty()) return;

    log.debug("Starting polling cycle for {} users", userIds.size()); 
    List<UUID> userUuid = userIds.stream()
      .map(UUID::fromString)
      .toList();

    List<UserConnection> connections = repository.findAllByUserIdInAndProvider(userUuid, ConnectionProvider.SPOTIFY);
  }
}
