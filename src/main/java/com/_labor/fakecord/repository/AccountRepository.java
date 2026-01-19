package com._labor.fakecord.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com._labor.fakecord.domain.entity.Account;

public interface AccountRepository extends JpaRepository<Account, UUID> {
  boolean existsByLogin(String login);
  boolean existsByEmail(String email);
}
