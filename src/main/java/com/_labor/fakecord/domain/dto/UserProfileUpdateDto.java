package com._labor.fakecord.domain.dto;

import com._labor.fakecord.domain.enums.ImageType;
import com._labor.fakecord.domain.enums.UserStatus;

import jakarta.validation.constraints.Size;
public record UserProfileUpdateDto(
  @Size (min = 3, max = 32, message = "Name has to be between 3 to 32 characters long!")
  String displayName,
  @Size(max = 254, message = "Bio cannot be longer then 254 characters long!")
  String bio,
  UserStatus statusPreference,
  ImageType avatarType,
  ImageType bannerType
){}
