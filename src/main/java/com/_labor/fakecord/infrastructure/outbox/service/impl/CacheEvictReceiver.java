package com._labor.fakecord.infrastructure.outbox.service.impl;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import com._labor.fakecord.infrastructure.outbox.domain.CacheEvictEvent;
import com._labor.fakecord.services.UserProfileCache;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CacheEvictReceiver {
  private final UserProfileCache userProfileCache;

  public CacheEvictReceiver(UserProfileCache userProfileCache) {
      this.userProfileCache = userProfileCache;
  }

  public void handleEvict(CacheEvictEvent event) {
    log.info("Received evict signal for aggregate: {}", event.aggregateId());

    if ("user_profiles".equals(event.cacheName())) {
      userProfileCache.evict(event.aggregateId());
      log.debug("Full L1/L2 eviction completed for {}", event.aggregateId());
    }
  }
}
