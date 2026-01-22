package com._labor.fakecord.services.impl;

import java.util.UUID;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com._labor.fakecord.repository.UserRepository;
import com._labor.fakecord.services.UserDetailsService;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  private final UserRepository repository;
  
  public UserDetailsServiceImpl(UserRepository repository) {
    this.repository = repository;
  }

  @Override
  public UserDetails loadUserByUserId(String subject) {
    UUID userId;
    try {
      userId = UUID.fromString(subject);
    } catch (IllegalArgumentException e) {
      throw new UsernameNotFoundException("Invalid User ID format: " + subject);
    }

    return repository.findById(userId)
      .map(user -> User
        .withUsername(user.getId().toString())
        .password("")
        .authorities("ROLE_USER")
        .build()
      )
      .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
  }
  
}
