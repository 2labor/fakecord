package com._labor.fakecord.services.impl;

import java.security.SecureRandom;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com._labor.fakecord.domain.entity.AuditAction;
import com._labor.fakecord.domain.entity.BackupCode;
import com._labor.fakecord.domain.entity.User;
import com._labor.fakecord.repository.BackupCodeRepository;
import com._labor.fakecord.services.AuditLogService;
import com._labor.fakecord.services.BackupCodeService;
import com._labor.fakecord.utils.HashUtil;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.transaction.annotation.Transactional;

@Service
public class BackupCodeServiceImpl implements BackupCodeService {

  private final BackupCodeRepository repository;
  private final AuditLogService auditLogService;
  private final PasswordEncoder passwordEncoder;
  private final SecureRandom secureRandom;

  public BackupCodeServiceImpl(
    BackupCodeRepository repository,
    AuditLogService auditLogService,
    PasswordEncoder passwordEncoder,
    SecureRandom secureRandom
  ) {
    this.repository = repository;
    this.auditLogService = auditLogService;
    this.passwordEncoder = passwordEncoder;
    this.secureRandom = secureRandom;
  }

  @Override
  @Transactional 
  public List<String> generateNewCodes(User user, HttpServletRequest request) {
    repository.deleteAllByUserId(user.getId());

    List<String> rawCodes = secureRandom.ints(10, 0, 100_000_000)
      .mapToObj(i -> String.format("%08d", i))
      .toList();

    List<BackupCode> entities = rawCodes.stream()
      .map(code -> BackupCode.builder()
        .user(user)
        .prefixHash(HashUtil.hashSha256(code.substring(0, 4)))
        .hashCode(passwordEncoder.encode(code))
        .build()
    ).toList();

    repository.saveAll(entities);

    auditLogService.log(user, AuditAction.BACKUP_CODE_GENERATED, request, null);

    return rawCodes;
  }

  @Override
  @Transactional
  public boolean verifyAndUseCode(User user, String rawCode, HttpServletRequest request) {
    if (null == rawCode || rawCode.length() != 8) {
      return false;
    }

    String prefix = rawCode.substring(0, 4);
    String prefixHash = HashUtil.hashSha256(prefix);

    List<BackupCode> candidates = repository.findAllByUserIdAndPrefixHashAndUsedAtIsNull(user.getId(), prefixHash);

    for (BackupCode code : candidates) {
      if (passwordEncoder.matches(rawCode, code.getHashCode())) {
        code.use();

        repository.save(code);

        auditLogService.log(user, AuditAction.BACKUP_CODE_USED_SUCCESS, request, null);
        return true;
      }
    }

    auditLogService.log(user, AuditAction.BACKUP_CODE_USED_FAILED, request, null);
    return false;
  }

  @Override
  @Transactional(readOnly = true)
  public long getRemainingCodesCount(User user) {
    return repository.countByUserIdAndUsedAtIsNull(user.getId());
  }
  
}
