package com._labor.fakecord.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
  @NotBlank(message = "Email field cannot be empty!")
  @Email(message = "Email have to be in valid form!")
  String email,
  @NotBlank(message = "Password field cannot be empty!")
  String password
) {}
