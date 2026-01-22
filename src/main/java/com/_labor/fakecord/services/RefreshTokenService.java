package com._labor.fakecord.services;

import java.util.Optional;
import java.util.UUID;

import com._labor.fakecord.domain.entity.RefreshToken;

public interface RefreshTokenService {
  RefreshToken createRefreshToken(UUID accountId);
  void deleteByUserId(UUID userId);
  public RefreshToken verifyExpiration(RefreshToken token);
  Optional<RefreshToken> findByToken(String token);
}
