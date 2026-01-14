package com._labor.fakecord.domain.mappper;

import com._labor.fakecord.domain.dto.UserDto;
import com._labor.fakecord.domain.entity.User;

public interface UserMapper {
  User fromDto(UserDto dto);
  UserDto toDto(User user);
}
