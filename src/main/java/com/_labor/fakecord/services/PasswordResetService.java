package com._labor.fakecord.services;

public interface PasswordResetService {
  void initiateReset(String email, String ip, String agent);
  void completeReset(String tokenValue, String newPassword, String ip, String agent);
} 