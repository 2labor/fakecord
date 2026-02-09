package com._labor.fakecord.domain.mappper.Impl;

import org.springframework.stereotype.Component;

import com._labor.fakecord.domain.dto.UserDto;
import com._labor.fakecord.domain.entity.EmailIdentity;
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

    EmailIdentity primaryIdentity = null;
    if (user.getEmailIdentities() != null) {
      primaryIdentity = user.getEmailIdentities().stream()
        .filter(EmailIdentity::isPrimary)
        .findFirst()
        .orElse(null);
    }

    String email = (primaryIdentity != null) ? primaryIdentity.getEmail() : null;
    boolean isVerified = (primaryIdentity != null) && primaryIdentity.isVerified();

    return new UserDto(
      user.getId(),
      user.getName(),
      email,
      isVerified
    );
  }
}
