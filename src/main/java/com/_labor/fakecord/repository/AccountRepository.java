package com._labor.fakecord.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com._labor.fakecord.domain.entity.Account;
import com._labor.fakecord.domain.entity.User;

public interface AccountRepository extends JpaRepository<Account, UUID> {
  Optional<Account> findByLogin(String login);
  Optional<Account> findByUser(User user);
  boolean existsByLogin(String login);
}
