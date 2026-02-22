package com._labor.fakecord.domain.dto;

import java.util.UUID;

import com._labor.fakecord.domain.enums.UserStatus;

import lombok.Builder;

@Builder
public record UserProfileShort(
  UUID userId,
  String displayName,
  String avatarUrl,
  UserStatus status
) {}