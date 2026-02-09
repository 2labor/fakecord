package com._labor.fakecord.services;

import java.util.UUID;

import com._labor.fakecord.domain.entity.User;

public interface EmailVerificationService {
  void sendConfirmationRequest(User user, String email);
  void confirmEmail(String tokenValue);
  void resendToPrimaryEmail(UUID userId);
}
