package com._labor.fakecord.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com._labor.fakecord.domain.entity.Account;

public interface AccountRepository extends JpaRepository<Account, UUID> {
  Optional<Account> findByLogin(String login);
  Optional<Account> findByEmail(String email);
  boolean existsByLogin(String login);
  boolean existsByEmail(String email);
}
