package com._labor.fakecord.services.impl;

import java.util.Objects;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com._labor.fakecord.domain.entity.Account;
import com._labor.fakecord.domain.entity.SocialAccount;
import com._labor.fakecord.domain.entity.TokenType;
import com._labor.fakecord.domain.entity.User;
import com._labor.fakecord.domain.entity.VerificationToken;
import com._labor.fakecord.domain.events.PasswordResetRequestedEvent;
import com._labor.fakecord.repository.AccountRepository;
import com._labor.fakecord.repository.SocialAccountRepository;
import com._labor.fakecord.services.PasswordResetService;
import com._labor.fakecord.services.VerificationTokenService;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class PasswordResetServiceImpl implements PasswordResetService{

  private final AccountRepository accountRepository;
  private final SocialAccountRepository socialAccountRepository;
  private final VerificationTokenService verificationTokenService; 
  private final ApplicationEventPublisher applicationEventPublisher;
  private final PasswordEncoder passwordEncoder;

  public PasswordResetServiceImpl(AccountRepository accountRepository, SocialAccountRepository socialAccountRepository,
      VerificationTokenService verificationTokenService, ApplicationEventPublisher applicationEventPublisher,
      PasswordEncoder passwordEncoder) {
    this.accountRepository = accountRepository;
    this.socialAccountRepository = socialAccountRepository;
    this.verificationTokenService = verificationTokenService;
    this.applicationEventPublisher = applicationEventPublisher;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  @Transactional
  public void initiateReset(String email, String ip, String agent) {
    User user = findUserByEmail(email);

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

    Account account = user.getAccount();
    if (null == account) {
      String email = findEmailForUser(user); 
      account = createAccountForSocialUser(user, email);
    }

    account.setPassword(passwordEncoder.encode(newPassword));
    accountRepository.save(account);

    verificationTokenService.deleteTokenSafe(verificationToken.getId(), TokenType.PASSWORD_RESET);

    log.info("Password reset completed for user: {}", user.getId());
  }
  

  private User findUserByEmail(String email) {
      return accountRepository.findByEmail(email)
        .map(Account::getUser)
        .orElseGet(() -> socialAccountRepository.findByEmail(email)
          .map(SocialAccount::getUser)
          .orElse(null));
  }

  private Account createAccountForSocialUser(User user, String email) {
    log.info("Creating local account for social user: {} with email: {}", user.getId(), email);

    Account newAccount = new Account();
    newAccount.setUser(user);
    newAccount.setEmail(email);
    
    String baseLogin = (user.getName() != null && !user.getName().isBlank()) 
                       ? user.getName() 
                       : email.split("@")[0];
                       
    if (accountRepository.existsByLogin(baseLogin)) {
        newAccount.setLogin(baseLogin + "_" + UUID.randomUUID().toString().substring(0, 5));
    } else {
        newAccount.setLogin(baseLogin);
    }

    return newAccount;
  }

  private String findEmailForUser(User user) {
    return user.getSocialAccounts().stream()
      .map(SocialAccount::getEmail)
      .filter(Objects::nonNull)
      .findFirst()
      .orElseThrow(() -> new IllegalStateException("Email not found"));
  }
}
