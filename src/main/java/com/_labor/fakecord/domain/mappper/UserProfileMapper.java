package com._labor.fakecord.domain.mappper;

import com._labor.fakecord.domain.dto.UserProfileFullDto;
import com._labor.fakecord.domain.dto.UserProfileShort;
import com._labor.fakecord.domain.dto.UserProfileUpdateDto;
import com._labor.fakecord.domain.dto.UserStatus;
import com._labor.fakecord.domain.entity.UserProfile;

public interface UserProfileMapper {
  UserProfileFullDto toFullDto(UserProfile profile, UserStatus status);
  UserProfileShort toShortDto(UserProfile profile, UserStatus status);
  void toUpdateDtp(UserProfileUpdateDto dto, UserProfile entity);
}
