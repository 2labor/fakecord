package com._labor.fakecord.domain.dto;

import java.util.UUID;

import lombok.Builder;

@Builder
public record UserProfileFullDto(
  UUID userId,
  String displayName,
  String bio,
  String avatarUrl,
  String bannerUrl,
  Object metadata,
  UserStatus status
) {}
