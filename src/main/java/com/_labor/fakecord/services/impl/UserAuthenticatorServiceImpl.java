package com._labor.fakecord.services.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com._labor.fakecord.domain.dto.MfaRegistrationResponse;
import com._labor.fakecord.domain.entity.AuthMethodType;
import com._labor.fakecord.domain.entity.User;
import com._labor.fakecord.domain.entity.UserAuthenticator;
import com._labor.fakecord.repository.UserAuthenticatorRepository;
import com._labor.fakecord.services.UserAuthenticatorService;
import com._labor.fakecord.utils.EncryptionUtil;
import com.warrenstrange.googleauth.GoogleAuthenticator;

@Service
public class UserAuthenticatorServiceImpl implements UserAuthenticatorService{

  private final UserAuthenticatorRepository repository;
  private final EncryptionUtil encryptionUtil;
  private final GoogleAuthenticator gAuth;

  public UserAuthenticatorServiceImpl(
    UserAuthenticatorRepository repository,
    EncryptionUtil encryptionUtil,
    GoogleAuthenticator gAuth
  ) {
    this.repository = repository;
    this.encryptionUtil = encryptionUtil;
    this.gAuth = gAuth;
  }

  @Override
  public List<UserAuthenticator> getActiveMethods(UUID userId) {
    return repository.findAllByUserIdAndIsActiveTrue(userId);
  }

  @Override
  @Transactional(readOnly = true)
  public boolean verifyCode(User user, AuthMethodType type, String code) {
    UserAuthenticator userAuthenticator = repository.findAllByUserIdAndIsActiveTrue(user.getId())
      .stream()
      .filter(a -> a.getType() == type)
      .findFirst()
      .orElseThrow(() -> new IllegalArgumentException("MFA method not found or inactive"));

    String plainSecret = encryptionUtil.decrypt(userAuthenticator.getSecretData()).trim();

    return switch(type) {
      case TOTP -> verifyTotp(plainSecret, code);
      default -> throw new UnsupportedOperationException("Method not supported!");
    };
  }

  @Override
  public boolean verifyTotp(String secret, String code) {
    try {
      int codeInt = Integer.parseInt(code);
      return gAuth.authorize(secret, codeInt);
    } catch (NumberFormatException e) {
      return false;
    }
  }

  @Override
  @Transactional
  public void enableMethod(User user, AuthMethodType type, String secretCode) {
    List<UserAuthenticator> oldMethods = repository.findAllByUserIdAndIsActiveTrue(user.getId());

    oldMethods.stream()
      .filter(a -> a.getType() == type)
      .map(UserAuthenticator::getId)
      .forEach(repository::deleteById);

    String encryptSecret = encryptionUtil.encrypt(secretCode);
    UserAuthenticator authenticator = UserAuthenticator.builder()
      .user(user)
      .type(type)
      .secretData(encryptSecret)
      .isActive(true)
      .build();

    repository.save(authenticator);
  }

  @Override
  @Transactional
  public void disableMethod(UUID userId, AuthMethodType type) {
    UserAuthenticator authenticator = repository.findAllByUserIdAndIsActiveTrue(userId)
      .stream()
      .filter(a -> a.getType() == type)
      .findFirst()
      .orElseThrow(() -> new IllegalArgumentException("No MFA with such type"));

    repository.delete(authenticator);
  }
  
  public MfaRegistrationResponse initiateMfaSetup(User user) {
    var key = gAuth.createCredentials();

    String qrCodeUri = String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s",
      "Fakecord", 
      user.getName(), 
      key.getKey(), 
      "Fakecord");
    
    return new MfaRegistrationResponse(key.getKey(), qrCodeUri);
  }
}
