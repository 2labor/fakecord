package com._labor.fakecord.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com._labor.fakecord.domain.entity.User;

import jakarta.transaction.Transactional;

public interface UserRepository extends JpaRepository<User, UUID> {
  Optional<User> findByName(String name);
  boolean existsByName(String name);
  @Query("SELECT u.securitySettings.tokenVersion FROM User u WHERE u.id = :id")
  Optional<Integer> findTokenVersionById(@Param("id") UUID id);
  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Transactional
  @Query("UPDATE User u SET u.securitySettings.tokenVersion = u.securitySettings.tokenVersion + 1 WHERE u.id = :id")
  void incrementTokenVersion(@Param("id") UUID id);
}
