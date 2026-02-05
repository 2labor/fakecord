package com._labor.fakecord.services;

import java.util.Optional;

import com._labor.fakecord.domain.entity.AuthProvider;
import com._labor.fakecord.domain.entity.EmailIdentity;
import com._labor.fakecord.domain.entity.User;

public interface IdentityService {
  EmailIdentity linkEmailToUser(User user, String email, AuthProvider provider, boolean verified, boolean isPrimary);
  boolean existByEmail(String email);
  Optional<EmailIdentity> findByEmail(String email);
  void verifyEmail(String email);
}
