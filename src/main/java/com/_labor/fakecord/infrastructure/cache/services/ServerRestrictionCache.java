package com._labor.fakecord.infrastructure.cache.services;

import java.util.Optional;
import java.util.UUID;

import com._labor.fakecord.infrastructure.cache.Dto.UserTimeoutCacheDto;

public interface ServerRestrictionCache {
  void put(UserTimeoutCacheDto dto);
  Optional<UserTimeoutCacheDto> get(Long serverId, UUID targetId);
  void evict(Long serverId, UUID targetId);  
}