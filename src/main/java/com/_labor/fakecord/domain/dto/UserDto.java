package com._labor.fakecord.domain.dto;

import java.util.UUID;

public record UserDto(
  UUID id,
  String name
) {
}