package com._labor.fakecord.infrastructure.cache.services.Impl;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com._labor.fakecord.infrastructure.cache.CacheProvider;
import com._labor.fakecord.infrastructure.cache.Dto.UserTimeoutCacheDto;
import com._labor.fakecord.infrastructure.cache.services.ServerRestrictionCache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServerRestrictionCacheImpl implements ServerRestrictionCache {

  private final CacheProvider cacheProvider;

  private static final String PREFIX_KEY = "server:timeout:";

  @Override
  public void put(UserTimeoutCacheDto dto) {
    if (dto.expiredAt() == null || dto.expiredAt() <= System.currentTimeMillis()) {
      log.warn("Attempted to cache expired or null timeout for user {} on server {}", dto.targetId(), dto.serverId());
      return;
    }

    String key = buildKey(dto.serverId(), dto.targetId());
    long millLeft = dto.expiredAt() - System.currentTimeMillis();
    Duration ttl = Duration.ofMillis(millLeft);

    cacheProvider.set(key, dto, ttl);
    log.debug("Timeout cached (L1+L2) with TTL {}s for key: {}", ttl.getSeconds(), key);    
  }

  @Override
  public Optional<UserTimeoutCacheDto> get(Long serverId, UUID targetId) {
    String key = buildKey(serverId, targetId);

    UserTimeoutCacheDto dto = cacheProvider.get(key, Duration.ZERO, UserTimeoutCacheDto.class, () -> null);
    return Optional.ofNullable(dto);
  }

  @Override
  public void evict(Long serverId, UUID targetId) {
    String key = buildKey(serverId, targetId);

    cacheProvider.evict(key);
    log.debug("Timeout cache evicted for key: {}", key);
  }

  private String buildKey(Long serverId, UUID targetId) {
    return PREFIX_KEY + serverId + ":" + targetId;
  }
}
