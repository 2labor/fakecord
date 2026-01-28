package com._labor.fakecord.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com._labor.fakecord.domain.entity.AuthProvider;
import com._labor.fakecord.domain.entity.SocialAccount;
import com._labor.fakecord.domain.entity.User;

@Repository
public interface SocialAccountRepository extends JpaRepository<SocialAccount, UUID> {
  Optional<SocialAccount> findByProviderAndProviderId(AuthProvider provider, String providerId);
  Optional<SocialAccount> findByUserAndProvider(User user, AuthProvider provider);
  Optional<SocialAccount> findByEmail(String email);
}
