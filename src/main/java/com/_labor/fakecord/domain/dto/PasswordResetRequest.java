package com._labor.fakecord.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record PasswordResetRequest(
  @Email(message = "Invalid form of email!")
  @NotBlank(message = "Email cannot be empty!")
  String email
) {} 