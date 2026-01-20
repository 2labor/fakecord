package com._labor.fakecord.services.impl;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com._labor.fakecord.domain.entity.Account;
import com._labor.fakecord.repository.AccountRepository;
import com._labor.fakecord.services.UserDetailsService;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  private final AccountRepository repository;
  
  public UserDetailsServiceImpl(AccountRepository repository) {
    this.repository = repository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) {
    Account account = repository.findByLogin(username)
      .orElseThrow(() -> new IllegalArgumentException("Account not found"));

    return User
      .withUsername(username)
      .password(account.getPassword())
      .authorities("USER")
      .build();
  }
  
}
