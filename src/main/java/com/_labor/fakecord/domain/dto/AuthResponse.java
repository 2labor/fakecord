package com._labor.fakecord.domain.dto;

import java.util.List;

import com._labor.fakecord.domain.entity.AuthMethodType;

import lombok.Builder;

@Builder
public record AuthResponse(
  String token,
  UserDto userDto,
  boolean mfaRequired,
  String sessionId,
  List<AuthMethodType> availableMethods
) {}
