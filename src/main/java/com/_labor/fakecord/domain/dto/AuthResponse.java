package com._labor.fakecord.domain.dto;

public record AuthResponse(
  String token,
  AccountDto accountDto
) {}
