package com._labor.fakecord.repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com._labor.fakecord.domain.entity.TokenType;
import com._labor.fakecord.domain.entity.VerificationToken;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, UUID>{
  Optional<VerificationToken> findByTokenAndType(String token, TokenType type);
  void deleteByUserIdAndType(UUID userId, TokenType type);
  void deleteByIdAndType(UUID userId , TokenType type);
  void deleteByUserId(UUID userId);
}
