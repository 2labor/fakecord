package com._labor.fakecord.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com._labor.fakecord.domain.entity.AuthMethodType;
import com._labor.fakecord.domain.entity.UserAuthenticator;

public interface UserAuthenticatorRepository extends JpaRepository<UserAuthenticator, UUID> {
  List<UserAuthenticator> findAllByUserIdAndIsActiveTrue(UUID userId);
  void deleteByUserIdAndType(UUID userId, AuthMethodType type);
  boolean existsByUserIdAndIsActiveTrue(UUID userId);
  void deleteByUserId(UUID userId);
}
