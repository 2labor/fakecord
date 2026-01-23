package com._labor.fakecord.services;

import java.util.List;
import java.util.UUID;

import com._labor.fakecord.domain.dto.MfaRegistrationResponse;
import com._labor.fakecord.domain.entity.AuthMethodType;
import com._labor.fakecord.domain.entity.User;
import com._labor.fakecord.domain.entity.UserAuthenticator;

public interface UserAuthenticatorService {
  List<UserAuthenticator> getActiveMethods(UUID userId);
  boolean verifyCode(User user, AuthMethodType type, String code);
  boolean verifyTotp(String secret, String code);
  void enableMethod(User user, AuthMethodType type, String secretCode);
  void disableMethod(UUID userId, AuthMethodType type);
  MfaRegistrationResponse initiateMfaSetup(User user);
}
