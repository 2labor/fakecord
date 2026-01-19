package com._labor.fakecord.services.impl;

import org.springframework.stereotype.Service;

import com._labor.fakecord.domain.dto.UserDto;
import com._labor.fakecord.domain.entity.User;
import com._labor.fakecord.domain.mappper.UserMapper;
import com._labor.fakecord.repository.UserRepository;
import com._labor.fakecord.services.UserServices;

import jakarta.transaction.Transactional;

@Service
public class UserServicesImpl implements UserServices {

  private final UserRepository repository;
  private final UserMapper mapper;

  public UserServicesImpl(UserRepository repository, UserMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  @Override
  @Transactional
  public UserDto createUser(UserDto userDto) {
    if (null == userDto) {
      throw new IllegalArgumentException("User details cannot be null");
    }

    User userEntity = mapper.fromDto(userDto);

    if (repository.existsByName(userEntity.getName())) {
      throw new IllegalArgumentException("Username '" + userEntity.getName() + "' is already taken");
    }

    User userSaved = repository.save(userEntity);
    
    return mapper.toDto(userSaved);
  } 
  
}
