package com._labor.fakecord.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com._labor.fakecord.domain.entity.EmailIdentity;
import com._labor.fakecord.domain.entity.User;

public interface EmailIdentityRepository extends JpaRepository<EmailIdentity, UUID>{
  Optional<EmailIdentity> findByEmail(String email);
  Optional<EmailIdentity> findByUserId(UUID userId);
  boolean existsByEmail(String email);
  Optional<EmailIdentity> findByUserAndIsPrimary(User user, boolean status);
  Optional<EmailIdentity> findByUserIdAndIsPrimary(UUID userId, boolean status);
}
