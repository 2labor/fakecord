package com._labor.fakecord.infrastructure.cache;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com._labor.fakecord.domain.dto.UserProfileFullDto;
import com._labor.fakecord.domain.dto.UserStatus;
import com._labor.fakecord.domain.entity.UserProfile;
import com._labor.fakecord.domain.mappper.UserProfileMapper;
import com._labor.fakecord.repository.UserProfileRepository;
import com._labor.fakecord.services.UserProfileCache;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserProfileCacheImpl implements UserProfileCache {

  private final UserProfileRepository repository;
  private final StringRedisTemplate redisTemplate;
  private final ObjectMapper objectMapper;
  private final UserProfileMapper mapper;
  private final Cache<UUID, UserProfileFullDto> localCache = Caffeine.newBuilder()
    .maximumSize(10000)
    .expireAfterWrite(5, TimeUnit.MINUTES)
    .build();

  private static final String REDIS_PREFIX = "profile:v1:";
  private static final Duration REDIS_TTL = Duration.ofHours(24);

  public UserProfileCacheImpl(UserProfileRepository repository, StringRedisTemplate redisTemplate, ObjectMapper objectMapper, UserProfileMapper mapper) {
    this.repository = repository;
    this.redisTemplate = redisTemplate;
    this.objectMapper = objectMapper;
    this.mapper = mapper;
  }

  @Override
  public UserProfileFullDto getUserProfile(UUID userId) {
    return localCache.get(userId, id -> {
      log.debug("L1 Cache miss for user {}", id);

      UserProfileFullDto cache = getFromRedis(userId);
      if (null != cache) return cache;

      log.info("L2 Cache miss for user {}. Fetching from DB", id);
      UserProfileFullDto dbProfile = repository.findById(userId)
        .map(entity -> mapper.toFullDto(entity, UserStatus.OFFLINE))
        .orElseGet(() -> createNegativeProfile(userId));

      saveToRedis(userId, dbProfile);
      return dbProfile;
    });
  }

  @Override
  public void evict(UUID userId) {
    log.info("Evicting profile cache for user {}", userId);
    localCache.invalidate(userId);
    redisTemplate.delete(REDIS_PREFIX + userId);
  }
  
  private UserProfileFullDto getFromRedis(UUID userId) {
    try {
      String json = redisTemplate.opsForValue().get(REDIS_PREFIX + userId);
      if (null == json) return null;
      return objectMapper.readValue(json, UserProfileFullDto.class);
    } catch (Exception e) {
      log.error("Failed to deserialize profile from Redis for user {}", userId, e);
      return null;
    }
  }


  private UserProfileFullDto createNegativeProfile(UUID userId) {
    return UserProfileFullDto.builder()
      .userId(userId)
      .displayName("Deleted User")
      .bio("This profile does not exist")
      .isGhost(true)
      .build();
  }

  private void saveToRedis(UUID userId, UserProfileFullDto profile) {
    try {
      String json = objectMapper.writeValueAsString(profile);
      redisTemplate.opsForValue().set(REDIS_PREFIX + userId, json, REDIS_TTL);
    } catch (Exception e) {
      log.error("Failed to serialize profile for Redis for user {}", userId, e);
    }
  }
}
