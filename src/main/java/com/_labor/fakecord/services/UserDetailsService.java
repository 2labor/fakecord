package com._labor.fakecord.services;

import org.springframework.security.core.userdetails.UserDetails;

public interface UserDetailsService {
  UserDetails loadUserByUserId(String subject);
}
