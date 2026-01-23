package com._labor.fakecord.domain.dto;

import com._labor.fakecord.domain.entity.AuthMethodType;
import com._labor.fakecord.domain.entity.TokenType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VerificationRequest(
  @NotBlank(message = "Code cannot be empty!")
  String code,
  @NotNull(message = "Token id cannot be null!")
  String tokenId,
  @NotNull(message = "Type cannot be null!")
  TokenType type,
  @NotNull(message = "Auth method type cannot be empty!")
  AuthMethodType authType
) {}
