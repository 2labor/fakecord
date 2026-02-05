package com._labor.fakecord.services.impl;

import java.time.Instant;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com._labor.fakecord.domain.entity.AuthProvider;
import com._labor.fakecord.domain.entity.EmailIdentity;
import com._labor.fakecord.domain.entity.User;
import com._labor.fakecord.repository.EmailIdentityRepository;
import com._labor.fakecord.services.IdentityService;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class IdentityServiceImpl implements IdentityService {

  private final EmailIdentityRepository repository;

  public IdentityServiceImpl(EmailIdentityRepository repository) {
    this.repository = repository;
  }

  @Override
  public EmailIdentity linkEmailToUser(User user, String email, AuthProvider provider, boolean verified,
      boolean isPrimary) {
    EmailIdentity identity = new EmailIdentity();
    identity.setUser(user);
    identity.setEmail(email);
    identity.setProvider(provider);
    identity.setVerified(verified);
    identity.setPrimary(isPrimary);

    if (verified) {
      identity.setVerifiedAt(Instant.now());
    }

    return repository.save(identity);
  }

  @Override
  public boolean existByEmail(String email) {
    return repository.existsByEmail(email);
  }

  @Override
  public Optional<EmailIdentity> findByEmail(String email) {
    return repository.findByEmail(email);
  }

  @Override
  @Transactional
  public void verifyEmail(String email) {
    repository.findByEmail(email).ifPresent(identity -> {
      identity.setVerified(true);
      identity.setVerifiedAt(Instant.now());
      log.info("Email {} marked as verified via OAuth2 provider", email);
    });
  }
}
