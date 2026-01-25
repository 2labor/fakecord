package com._labor.fakecord.services;

import java.util.List;

import com._labor.fakecord.domain.entity.User;

import jakarta.servlet.http.HttpServletRequest;

public interface BackupCodeService {
  List<String> generateNewCodes(User user, HttpServletRequest request);
  boolean verifyAndUseCode(User user, String rawCode, HttpServletRequest request);
  long getRemainingCodesCount(User user);
}
