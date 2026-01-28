package com._labor.fakecord.services;

import java.util.Optional;
import java.util.UUID;

import com._labor.fakecord.domain.entity.TokenType;
import com._labor.fakecord.domain.entity.User;
import com._labor.fakecord.domain.entity.VerificationToken;

public interface VerificationTokenService {
  VerificationToken createToken(User user, TokenType type, String ip, String agent);
  Optional<VerificationToken> verifyToken(String code, TokenType type, String currentIp, String currentAgent);
  void recordFailedAttempt(String tokenId);
  void deleteTokenSafe(UUID tokenId, TokenType expectedType);
}