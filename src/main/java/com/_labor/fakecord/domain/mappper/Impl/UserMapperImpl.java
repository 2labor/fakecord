package com._labor.fakecord.domain.mappper.Impl;

import org.springframework.stereotype.Component;

import com._labor.fakecord.domain.dto.UserDto;
import com._labor.fakecord.domain.entity.User;
import com._labor.fakecord.domain.mappper.UserMapper;

@Component
public class UserMapperImpl implements UserMapper {

  @Override
  public User fromDto(UserDto dto) {
    if (null == dto) return null;

    User user = new User();
    user.setId(dto.id());
    user.setName(dto.name());

    return user;
  }

  @Override
  public UserDto toDto(User user) {
    if (user == null) return null;

    String primaryEmail = null;
    if (user.getEmailIdentities() != null) {
      primaryEmail = user.getEmailIdentities().stream()
        .filter(identity -> identity.isPrimary())
        .map(identity -> identity.getEmail())
        .findFirst()
        .orElse(null);
    }

    return new UserDto(
      user.getId(),
      user.getName(),
      primaryEmail
    );
  }
}
