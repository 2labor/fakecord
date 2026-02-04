package com._labor.fakecord.infrastructure;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com._labor.fakecord.domain.entity.User;
import com._labor.fakecord.security.JwtCore;
import com._labor.fakecord.security.versions.TokenVersionManager;
import com._labor.fakecord.services.RefreshTokenService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TokenProvider {
  private final TokenVersionManager versionManager;
  private final JwtCore jwtCore;
  private final RefreshTokenService refreshTokenService;

  public TokenProvider(TokenVersionManager versionManager, JwtCore jwtCore, RefreshTokenService refreshTokenService) {
    this.versionManager =  versionManager;
    this.jwtCore = jwtCore;
    this.refreshTokenService = refreshTokenService;
  }

  public String createAccessToken(UUID userId) {
    if (null == userId) {
      throw new SecurityException("Cannot generate token for null user");
    }

    int version = versionManager.getCurrentVersion(userId);

    log.debug("Generating access token for user {} with version {}", userId, version);

    return jwtCore.generateToken(userId, version);
  }

  public void invalidateAllSessions(UUID userId) {
    versionManager.evictVersion(userId);
  }

  public void removeAllAccess(UUID userId) {
    versionManager.evictVersion(userId);
    refreshTokenService.deleteByUserId(userId);
  }
}
