package com._labor.fakecord.domain.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
  @NotBlank(message = "Login field cannot be empty!")
  String login,
  @NotBlank(message = "Password field cannot be empty!")
  String password
) {}
