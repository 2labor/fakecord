package com._labor.fakecord.infrastructure;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com._labor.fakecord.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserSecurityService {
  private final TokenProvider tokenProvider;
  private final UserRepository userRepository;
  
  public UserSecurityService(TokenProvider tokenProvider, UserRepository userRepository) {
    this.tokenProvider = tokenProvider;
    this.userRepository = userRepository;
  }

  @Transactional
  public void resetUserSecurityEpoch(UUID userId) {
    userRepository.incrementTokenVersion(userId);
    tokenProvider.removeAllAccess(userId);
  }

}
