package com._labor.fakecord.services.impl;

import java.time.Duration;
import java.util.UUID;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com._labor.fakecord.services.UserStatusService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RedisUserStatusService implements UserStatusService {
  
  private final StringRedisTemplate redisTemplate;

  private static final String STATUS_KEY_PREFIX = "status:";

  public RedisUserStatusService(StringRedisTemplate redisTemplate) {
    this.redisTemplate = redisTemplate;
  } 
  
  @Override
  public void setOnline(UUID userId) {
    String key = STATUS_KEY_PREFIX + userId;

    redisTemplate.opsForValue().set(key, "ONLINE", Duration.ofHours(12));
    log.trace("Status heartbeated for user: {}", userId);
    
  }

  @Override
  public void setOffline(UUID userId) {
    redisTemplate.delete(STATUS_KEY_PREFIX + userId);
    log.debug("User {} manual logout/disconnect", userId);
  }

  @Override
  public boolean isOnline(UUID userId) {
    return Boolean.TRUE.equals(redisTemplate.hasKey(STATUS_KEY_PREFIX + userId));
  }
    
}
