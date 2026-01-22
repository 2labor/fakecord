package com._labor.fakecord.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com._labor.fakecord.domain.entity.Account;
import com._labor.fakecord.domain.entity.AuthProvider;
import com._labor.fakecord.domain.entity.SocialAccount;

public interface SocialAccountRepository extends JpaRepository<SocialAccount, UUID> {
  Optional<SocialAccount> findByProviderAndProviderId(AuthProvider provider, String providerId);
}
