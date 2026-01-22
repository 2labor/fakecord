package com._labor.fakecord.services.impl;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com._labor.fakecord.domain.entity.RefreshToken;
import com._labor.fakecord.domain.entity.User;
import com._labor.fakecord.repository.RefreshTokenRepository;
import com._labor.fakecord.repository.UserRepository;
import com._labor.fakecord.services.RefreshTokenService;

import jakarta.transaction.Transactional;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {


  @Value("${fakecord.jwt.refreshExpirationMs}")
  private long refreshTokenDurationMs;

  private final RefreshTokenRepository repository;
  private final UserRepository userRepository;

  public RefreshTokenServiceImpl(RefreshTokenRepository repository, UserRepository userRepository) {
    this.repository = repository;
    this.userRepository = userRepository;
  }

  @Transactional
  @Override
  public RefreshToken createRefreshToken(UUID userId) {
    User user = userRepository.findById(userId)
      .orElseThrow(() -> new IllegalArgumentException("User not found!"));

    deleteByUserId(userId);

    RefreshToken refreshToken = RefreshToken.builder()
      .user(user)
      .token(UUID.randomUUID().toString())
      .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
      .build();

    return repository.save(refreshToken);
  }

  @Transactional
  @Override
  public void deleteByUserId(UUID userId) {
    repository.deleteByUserId(userId);
  }

  @Override
  public RefreshToken verifyExpiration(RefreshToken token) {
    if (token.getExpiryDate().isBefore(Instant.now())) {
      throw new RuntimeException("Refreshing token get expired, please login again!");
    }

    token.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
    return repository.save(token);
  }

  @Override
  public Optional<RefreshToken> findByToken(String token) {
    return repository.findByToken(token);
  }

  
  
}
