package com._labor.fakecord.domain.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserDto(
  UUID id,
  @NotBlank(message = "name cannot be blank")
  @Size(min = 3, max = 24, message = "Size of a name should 3-24 characters")
  String name,
  String email,
  boolean isVerified
) {
}