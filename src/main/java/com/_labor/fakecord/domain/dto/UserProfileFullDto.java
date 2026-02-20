package com._labor.fakecord.domain.dto;

import java.util.UUID;

import lombok.Builder;

@Builder(toBuilder = true)
public record UserProfileFullDto(
  UUID userId,
  String displayName,
  String bio,
  String avatarUrl,
  String bannerUrl,
  Object metadata,
  UserStatus status,
  boolean isGhost
) {
  public static class UserProfileFullDtoBuilder {
    private boolean isGhost = false; 
  }
}
