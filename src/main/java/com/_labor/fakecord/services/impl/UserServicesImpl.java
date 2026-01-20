package com._labor.fakecord.services.impl;

import org.springframework.stereotype.Service;

import com._labor.fakecord.domain.mappper.UserMapper;
import com._labor.fakecord.repository.UserRepository;
import com._labor.fakecord.services.UserServices;


@Service
public class UserServicesImpl implements UserServices {

  private final UserRepository repository;
  private final UserMapper mapper;

  public UserServicesImpl(UserRepository repository, UserMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }
  
}
