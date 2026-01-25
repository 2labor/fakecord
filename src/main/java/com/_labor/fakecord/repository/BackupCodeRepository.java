package com._labor.fakecord.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com._labor.fakecord.domain.entity.BackupCode;

public interface BackupCodeRepository extends JpaRepository<BackupCode, UUID>{
  List<BackupCode> findAllByUserIdAndPrefixHashAndUsedAtIsNull(UUID userId, String prefixHash);
  void deleteAllByUserId(UUID userId);
  long countByUserIdAndUsedAtIsNull(UUID userId);
}
