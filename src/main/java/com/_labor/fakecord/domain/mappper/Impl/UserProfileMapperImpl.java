package com._labor.fakecord.domain.mappper.Impl;

import org.springframework.stereotype.Component;

import com._labor.fakecord.domain.dto.UserProfileFullDto;
import com._labor.fakecord.domain.dto.UserProfileShort;
import com._labor.fakecord.domain.dto.UserProfileUpdateDto;
import com._labor.fakecord.domain.dto.UserStatus;
import com._labor.fakecord.domain.entity.UserProfile;
import com._labor.fakecord.domain.mappper.UserProfileMapper;

@Component
public class UserProfileMapperImpl implements UserProfileMapper {

  @Override
  public UserProfileFullDto toFullDto(UserProfile profile, UserStatus status) {
    if (null == profile) return null;

    return UserProfileFullDto.builder()
      .userId(profile.getId())
      .displayName(profile.getDisplayName())
      .bio(profile.getBio())
      .avatarUrl(profile.getAvatarUrl())
      .bannerUrl(profile.getBannerUrl())
      .metadata(profile.getMetadata())
      .status(status != null ? status : UserStatus.OFFLINE)
      .build();
  }

  @Override
  public UserProfileShort toShortDto(UserProfile profile, UserStatus status) {
    if (null == profile) return null;

    return UserProfileShort.builder()
      .userId(profile.getId())
      .displayName(profile.getDisplayName())
      .avatarUrl(profile.getAvatarUrl())
      .status(status != null ? status : UserStatus.OFFLINE)
      .build();
  }

  @Override
  public void toUpdateDtp(UserProfileUpdateDto dto, UserProfile entity) {
    if (null == dto) return;
    if (null != dto.displayName()) {
      entity.setDisplayName(dto.displayName());
    }
    if (null != dto.bio()) {
      entity.setBio(dto.bio());
    }
    if (null != dto.avatarUrl()) {
      entity.setAvatarUrl(dto.avatarUrl());
    }
    if (null != dto.bannerUrl()) {
      entity.setBannerUrl(dto.bannerUrl());
    }
  }

  @Override
  public UserProfileShort toShortDto(UserProfileFullDto full, UserStatus status) {
    if (null == full) return null;

    return UserProfileShort.builder()
      .userId(full.userId())
      .displayName(full.displayName())
      .avatarUrl(full.avatarUrl())
      .status(status != null ? status : full.status())
      .build();
  }
}