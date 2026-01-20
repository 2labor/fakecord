package com._labor.fakecord.services;

import com._labor.fakecord.domain.dto.AuthResponse;
import com._labor.fakecord.domain.dto.RegisterRequest;

public interface AuthService {
  AuthResponse register(RegisterRequest request);
}
