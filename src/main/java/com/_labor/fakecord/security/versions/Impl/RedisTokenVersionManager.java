package com._labor.fakecord.security.versions.Impl;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com._labor.fakecord.repository.UserRepository;
import com._labor.fakecord.security.versions.TokenVersionManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RedisTokenVersionManager implements TokenVersionManager {

  private final StringRedisTemplate redisTemplate;
  private final UserRepository userRepository;
  private final Cache<UUID, Integer> localCache;
  
  private final static String KEY_PREFIX = "auth:v:";
  private final static Duration CACHE_TTL = Duration.ofHours(24);

  public RedisTokenVersionManager(StringRedisTemplate redisTemplate, UserRepository userRepository) {
    this.redisTemplate = redisTemplate;
    this.userRepository = userRepository;

    this.localCache = Caffeine.newBuilder()
      .expireAfterWrite(1, TimeUnit.MINUTES)
      .maximumSize(10_000)
      .build();
  }

  @Override
  public int getCurrentVersion(UUID userId) {
    return localCache.get(userId, key -> {
      String cached = redisTemplate.opsForValue().get("auth:v:" + key);
      if (cached != null) {
          return Integer.parseInt(cached);
      }

      int dbVersion = userRepository.findTokenVersionById(key)
          .orElseThrow(() -> new RuntimeException("User not found"));
      
      updateRedisCache(key, dbVersion);
      return dbVersion;
  });
  }

  @Override
  public void evictVersion(UUID userId) {
    localCache.invalidate(userId);
    redisTemplate.delete(KEY_PREFIX + userId);
  }

  @Override
  public void updateRedisCache(UUID userId, int version) {
    try {
      redisTemplate.opsForValue().set(KEY_PREFIX + userId, String.valueOf(version), CACHE_TTL);
    } catch (Exception e) {
      log.error("Failed to update Redis cache for user {}", userId);
    }
  }
  
}
