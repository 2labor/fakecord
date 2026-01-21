package com._labor.fakecord.services.impl;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com._labor.fakecord.domain.entity.Account;
import com._labor.fakecord.domain.entity.RefreshToken;
import com._labor.fakecord.repository.AccountRepository;
import com._labor.fakecord.repository.RefreshTokenRepository;
import com._labor.fakecord.security.TokenFilter;
import com._labor.fakecord.services.RefreshTokenService;

import jakarta.transaction.Transactional;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {


  @Value("${fakecord.jwt.refreshExpirationMs}")
  private long refreshTokenDurationMs;

  private final RefreshTokenRepository repository;
  private final AccountRepository accountRepository;

  public RefreshTokenServiceImpl(RefreshTokenRepository repository, AccountRepository accountRepository) {
    this.repository = repository;
    this.accountRepository = accountRepository;
  }

  @Transactional
  @Override
  public RefreshToken createRefreshToken(UUID accountId) {
    Account account = accountRepository.findById(accountId)
      .orElseThrow(() -> new IllegalArgumentException("Account not found!"));

    deleteByAccount(account);

    RefreshToken refreshToken = RefreshToken.builder()
      .account(account)
      .token(UUID.randomUUID().toString())
      .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
      .build();

    return repository.save(refreshToken);
  }

  @Transactional
  @Override
  public void deleteByAccount(Account account) {
    repository.deleteByAccount(account);
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
