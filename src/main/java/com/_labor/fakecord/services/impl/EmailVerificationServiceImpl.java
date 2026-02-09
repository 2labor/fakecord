package com._labor.fakecord.services.impl;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com._labor.fakecord.domain.entity.TokenType;
import com._labor.fakecord.domain.entity.User;
import com._labor.fakecord.domain.entity.VerificationToken;
import com._labor.fakecord.repository.EmailIdentityRepository;
import com._labor.fakecord.repository.VerificationTokenRepository;
import com._labor.fakecord.services.EmailVerificationService;
import com._labor.fakecord.services.MailService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmailVerificationServiceImpl implements EmailVerificationService{

  private final VerificationTokenRepository tokenRepository;
  private final EmailIdentityRepository emailIdentityRepository;
  private final MailService mailService;

  @Value("${fakecord.auth.verification-ttl-hours:24}")
  private int tokenTtlHours;

  public EmailVerificationServiceImpl(
    VerificationTokenRepository tokenRepository,
    EmailIdentityRepository emailIdentityRepository,
    MailService mailService
  ) {
    this.tokenRepository = tokenRepository;
    this.emailIdentityRepository = emailIdentityRepository;
    this.mailService = mailService;
  }

  @Override
  @Transactional
  public void sendConfirmationRequest(User user, String email) {
    tokenRepository.deleteByUserIdAndType(user.getId(), TokenType.EMAIL_CONFIRM);

    String tokenValue = UUID.randomUUID().toString();
    VerificationToken token = VerificationToken.builder()
      .token(tokenValue)
      .user(user)
      .type(TokenType.EMAIL_CONFIRM)
      .expiringDate(Instant.now().plus(Duration.ofHours(tokenTtlHours)))
      .build();

    tokenRepository.save(token);
    mailService.sendDiscordStyleConfirmation(email, user.getName(), tokenValue);
  }

  @Override
  public void confirmEmail(String tokenValue) {
    VerificationToken token = tokenRepository.findByTokenAndType(tokenValue, TokenType.EMAIL_CONFIRM)
      .orElseThrow(() -> new IllegalArgumentException("Invalid link or token has been expired!"));

    if (token.isExpired()) {
      tokenRepository.delete(token);
      throw new IllegalArgumentException("Link has been expired, pleas resend a new one!");
    }

    var identity = emailIdentityRepository.findByUserAndIsPrimary(token.getUser(), true)
      .orElseThrow(() -> new IllegalArgumentException("Primary email not found!"));

    identity.setVerified(true);
    identity.setVerifiedAt(Instant.now());
    emailIdentityRepository.save(identity);

    tokenRepository.delete(token);
    log.info("Email confirmed for user: {}", token.getUser().getId());
  }

  @Override
  @Transactional
  public void resendToPrimaryEmail(UUID userId) {
    var primaryEmail = emailIdentityRepository.findByUserIdAndIsPrimary(userId, true)
      .orElseThrow(() -> new EntityNotFoundException("Primary email not found."));

      if (primaryEmail.isVerified()) {
        throw new IllegalStateException("Your email is already verified.");
      }

      sendConfirmationRequest(primaryEmail.getUser(), primaryEmail.getEmail());
      
      log.info("Resent confirmation to primary email for user: {}", userId);
  }
  
}
