package com._labor.fakecord.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
  @NotBlank(message = "Account login cannot be empty")
  @Size(min = 3, max = 24, message = "Login for account have to be in range(3-24) character")
  String login,
  @NotBlank(message = "Account email cannot be empty!")
  @Email(message = "Invalid form for email!")
  String email,
  @NotBlank(message = "Account password cannot be empty!")
  @Size(min = 6, message = "Password should contains minimum 6 characters")
  String password,
  @NotBlank(message = "name cannot be blank")
  @Size(min = 3, max = 24, message = "Size of a name should 3-24 characters")
  String userName
) {}
