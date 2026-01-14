package com._labor.fakecord.services.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com._labor.fakecord.domain.entity.User;
import com._labor.fakecord.repository.UserRepository;
import com._labor.fakecord.services.UserServices;

@Service
public class UserServicesImpl implements UserServices {

  private final UserRepository repository;

  public UserServicesImpl(UserRepository repository) {
    this.repository = repository;
  }

  @Override
  public User createUser(User user) {
    if (null != user.getId()) {
      throw new IllegalArgumentException("User already heve an id");
    }
    
    if (null == user.getName()) {
      throw new IllegalArgumentException("User have to have username");
    }

    LocalDateTime now = LocalDateTime.now();
    return repository.save(new User(
      null,
      user.getName(),
      now,
      now));
  }
  
}
