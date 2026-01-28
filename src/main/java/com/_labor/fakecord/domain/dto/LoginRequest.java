package com._labor.fakecord.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
  @NotBlank(message = "Login/Email cannot be empty!")
  String identifier,
  @NotBlank(message = "Password field cannot be empty!")
  String password
) {}
