package com._labor.fakecord.domain.dto;

import jakarta.validation.constraints.NotBlank;

public record MfaSetupRequest(
  @NotBlank(message = "Code is required")
  String code,
  @NotBlank(message = "Secret is required")
  String secret
) {}
