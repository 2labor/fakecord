package com._labor.fakecord.services.impl;

import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com._labor.fakecord.domain.entity.Account;
import com._labor.fakecord.domain.entity.EmailIdentity;
import com._labor.fakecord.domain.entity.TokenType;
import com._labor.fakecord.domain.entity.User;
import com._labor.fakecord.domain.entity.VerificationToken;
import com._labor.fakecord.domain.events.PasswordResetRequestedEvent;
import com._labor.fakecord.infrastructure.UserSecurityService;
import com._labor.fakecord.repository.AccountRepository;
import com._labor.fakecord.services.IdentityService;
import com._labor.fakecord.services.PasswordResetService;
import com._labor.fakecord.services.VerificationTokenService;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class PasswordResetServiceImpl implements PasswordResetService{

  private final AccountRepository accountRepository;
  private final VerificationTokenService verificationTokenService; 
  private final ApplicationEventPublisher applicationEventPublisher;
  private final PasswordEncoder passwordEncoder;
  private final UserSecurityService userSecurityService;
  private final IdentityService identityService;

  public PasswordResetServiceImpl(AccountRepository accountRepository,
      VerificationTokenService verificationTokenService, ApplicationEventPublisher applicationEventPublisher,
      PasswordEncoder passwordEncoder, UserSecurityService userSecurityService, IdentityService identityService) {
    this.accountRepository = accountRepository;
    this.verificationTokenService = verificationTokenService;
    this.applicationEventPublisher = applicationEventPublisher;
    this.passwordEncoder = passwordEncoder;
    this.userSecurityService = userSecurityService;
    this.identityService = identityService;
  }

  @Override
  @Transactional
  public void initiateReset(String email, String ip, String agent) {
    User user = identityService.findByEmail(email)
      .map(EmailIdentity::getUser)
      .orElse(null);

    if (null != user) {
      var token = verificationTokenService.createToken(user, TokenType.PASSWORD_RESET, ip, agent);

      applicationEventPublisher.publishEvent(new PasswordResetRequestedEvent(this, email, token.getToken(), user.getId()));

      log.info("Reset email sent to existing user identity: {}", user.getId());
    } else {
      log.warn("Reset requested for unknown email: {}", email);
    }
  }

  @Override
  @Transactional
  public void completeReset(String tokenValue, String newPassword, String ip, String agent) {
    VerificationToken verificationToken = verificationTokenService.verifyToken(tokenValue, TokenType.PASSWORD_RESET, ip, agent)
      .orElseThrow(() -> new IllegalArgumentException("Invalid token!"));

    User user = verificationToken.getUser();

    Account account = accountRepository.findByUser(user)
      .orElseGet(() -> createAccountForSocialUser(user));

    account.setPassword(passwordEncoder.encode(newPassword));
    accountRepository.save(account);

    userSecurityService.resetUserSecurityEpoch(user.getId());

    verificationTokenService.deleteTokenSafe(verificationToken.getId(), TokenType.PASSWORD_RESET);

    log.info("Password reset completed for user: {}", user.getId());
  }
  
  private Account createAccountForSocialUser(User user) {
    log.info("Creating local account for social user: {} ", user.getId());

    Account newAccount = new Account();
    newAccount.setUser(user);
    
    String baseLogin = (user.getName() != null && !user.getName().isBlank()) 
                       ? user.getName() 
                       : "user_" + user.getId().toString().substring(0, 8);
                       
    if (accountRepository.existsByLogin(baseLogin)) {
        newAccount.setLogin(baseLogin + "_" + UUID.randomUUID().toString().substring(0, 5));
    } else {
        newAccount.setLogin(baseLogin);
    }

    return newAccount;
  }
}
