package com._labor.fakecord.domain.dto;

import lombok.Builder;

@Builder
public record AuthResponse(
  String token,
  UserDto userDto
) {}
