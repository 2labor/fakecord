package com._labor.fakecord.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PasswordResetConfirm(
  @NotBlank(message = "Token cannot be blank!")
  String token,
  @Size(min = 8, max = 64, message = "Password have to be in range 8-64 characters!")
  String newPassword
) {}