package com._labor.fakecord.domain.mappper;

import java.util.List;

import com._labor.fakecord.domain.dto.UserProfileFullDto;
import com._labor.fakecord.domain.dto.UserProfileShort;
import com._labor.fakecord.domain.dto.UserProfileUpdateDto;
import com._labor.fakecord.domain.entity.UserConnection;
import com._labor.fakecord.domain.entity.UserProfile;
import com._labor.fakecord.domain.enums.UserStatus;

public interface UserProfileMapper {
  UserProfileFullDto toFullDto(UserProfile profile, UserStatus status, List<UserConnection> connections);
  UserProfileShort toShortDto(UserProfile profile, UserStatus status);
  UserProfileShort toShortDto(UserProfileFullDto full, UserStatus status);
  void toUpdateDto(UserProfileUpdateDto dto, UserProfile entity);
}
