package com._labor.fakecord.domain.mappper.Impl;

import org.springframework.stereotype.Component;

import com._labor.fakecord.domain.dto.UserDto;
import com._labor.fakecord.domain.entity.User;
import com._labor.fakecord.domain.mappper.UserMapper;

@Component
public class UserMapperImpl implements UserMapper {

  @Override
  public User fromDto(UserDto dto) {
    return new User(
      dto.id(),
      dto.name(), 
      null, 
      null);
  }

  @Override
  public UserDto toDto(User user) {
    return new UserDto(
      user.getId(),
      user.getName()
    );
  }
  
}
