package com._labor.fakecord.services.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com._labor.fakecord.domain.dto.UserProfileFullDto;
import com._labor.fakecord.domain.dto.UserProfileUpdateDto;
import com._labor.fakecord.domain.dto.UserStatus;
import com._labor.fakecord.domain.entity.UserProfile;
import com._labor.fakecord.domain.mappper.UserProfileMapper;
import com._labor.fakecord.exception.ProfileNotFoundException;
import com._labor.fakecord.repository.UserProfileRepository;
import com._labor.fakecord.services.UserProfileServices;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserProfileServicesImpl implements UserProfileServices{
  
  private final UserProfileMapper mapper;
  private final UserProfileRepository repository;

  public UserProfileServicesImpl(UserProfileMapper mapper, UserProfileRepository repository) {
    this.mapper = mapper;
    this.repository = repository;
  }
  
  @Override
  @Transactional(readOnly = true)
  public UserProfileFullDto getById(UUID userId) {
    return repository.findById(userId)
      .map(profile -> mapper.toFullDto(profile, UserStatus.OFFLINE))
      .orElseThrow(() -> new ProfileNotFoundException(userId));
  }

  @Override
  @Transactional
  public UserProfileFullDto update(UUID userId, UserProfileUpdateDto updateDto) {
    UserProfile profile = repository.findById(userId)
      .orElseThrow(() -> new ProfileNotFoundException(userId));
    
    mapper.toUpdateDtp(updateDto, profile);

    return mapper.toFullDto(profile, UserStatus.OFFLINE);
  }
  
}
