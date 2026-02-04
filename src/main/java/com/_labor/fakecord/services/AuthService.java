package com._labor.fakecord.services;

import java.util.UUID;

import com._labor.fakecord.domain.dto.AuthResponse;
import com._labor.fakecord.domain.dto.LoginRequest;
import com._labor.fakecord.domain.dto.RegisterRequest;
import com._labor.fakecord.domain.dto.VerificationRequest;

public interface AuthService {
  AuthResponse register(RegisterRequest request);
  AuthResponse login(LoginRequest request, String ip, String agent);
  AuthResponse verify(VerificationRequest request, String ip, String agent);
  void logoutEverywhere(UUID userId);
}
