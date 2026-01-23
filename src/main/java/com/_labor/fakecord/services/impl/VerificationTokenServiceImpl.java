package com._labor.fakecord.services.impl;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com._labor.fakecord.domain.entity.TokenType;
import com._labor.fakecord.domain.entity.User;
import com._labor.fakecord.domain.entity.VerificationToken;
import com._labor.fakecord.repository.VerificationTokenRepository;
import com._labor.fakecord.services.VerificationTokenService;

import jakarta.transaction.Transactional;

@Service
public class VerificationTokenServiceImpl implements VerificationTokenService {

  private final VerificationTokenRepository repository;
  private final SecureRandom secureRandom;

  @Value("${fakecord.mfa.duration-minutes:5}")
  private int mfaDuration;

  @Value("${fakecord.mfa.max-attempts:3}")
  private int mfaMaxAttempts;

  @Value("${fakecord.email.duration-hours:24}")
  private int emailDuration;

  public VerificationTokenServiceImpl(VerificationTokenRepository repository, SecureRandom secureRandom) {
    this.repository = repository;
    this.secureRandom = secureRandom;
  }

  @Override
  @Transactional
  public VerificationToken createToken(User user, TokenType type, String ip, String agent) {
    repository.deleteByUserIdAndType(user.getId(), type);

    String value;
    Instant expiry;
    switch (type) {
      case MFA_SESSION -> {
        value = String.valueOf(100000 + secureRandom.nextInt(900000));
        expiry = Instant.now().plus(Duration.ofMinutes(mfaDuration));
      }
      case EMAIL_CONFIRM, PASSWORD_RESET -> {
        value = UUID.randomUUID().toString();
        expiry = Instant.now().plus(Duration.ofHours(emailDuration));
      } 
      default -> throw new IllegalArgumentException("Invalid token type: " + type);
    }
    VerificationToken token = VerificationToken.builder()
      .token(value)
      .user(user)
      .type(type)
      .expiringDate(expiry)
      .ipAddress(ip)
      .userAgent(agent)
      .build();

    return repository.save(token);
  }

  @Override
  @Transactional
  public Optional<VerificationToken> verifyToken(String tokenId, TokenType type, String currentIp, String currentAgent) {
    return repository.findById(UUID.fromString(tokenId))
      .filter(token -> token.getType() == type)
      .filter(token -> {
        if (token.isExpired() || token.getAttempts() >= mfaMaxAttempts) {
          repository.delete(token);
          return false;
        }
        if (!token.getIpAddress().equals(currentIp) || !token.getUserAgent().equals(currentAgent)) {
          repository.delete(token);
          return false;
        }

        if (type == TokenType.MFA_SESSION) {
            return true; 
        }

        return true; 
      });
  } 

  @Override
  @Transactional
  public void recordFailedAttempt(String tokenId) {
    repository.findById(UUID.fromString(tokenId)).ifPresent(token -> {
      token.incrementAttempts();
      if (token.getAttempts() >= mfaMaxAttempts) {
        repository.delete(token);
      } else {
        repository.save(token);
      }
    });
  }

}
