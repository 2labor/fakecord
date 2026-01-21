package com._labor.fakecord.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import com._labor.fakecord.domain.entity.Account;
import com._labor.fakecord.domain.entity.RefreshToken;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
  Optional<RefreshToken> findByToken(String token);
  
  @Modifying
  void deleteByAccount(Account account);
}