package com._labor.fakecord.domain.dto;

import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AccountDto(
  @NotNull(message = "Account wasn't found")
  UUID id,
  @NotEmpty(message = "Account login cannot be empty")
  @Size(min = 3, max = 24, message = "Login for account have to be in range(3-24) character")
  String login,
  @NotEmpty(message = "Account email cannot be empty!")
  @Email(message = "Invalid form for email!")
  String email,
  @Valid UserDto userDto
) {}
