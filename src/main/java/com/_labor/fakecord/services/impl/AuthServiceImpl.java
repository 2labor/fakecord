package com._labor.fakecord.services.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com._labor.fakecord.domain.dto.AuthResponse;
import com._labor.fakecord.domain.dto.LoginRequest;
import com._labor.fakecord.domain.dto.RegisterRequest;
import com._labor.fakecord.domain.dto.UserDto;
import com._labor.fakecord.domain.entity.Account;
import com._labor.fakecord.domain.entity.User;
import com._labor.fakecord.domain.mappper.UserMapper;
import com._labor.fakecord.repository.AccountRepository;
import com._labor.fakecord.security.JwtCore;
import com._labor.fakecord.services.AuthService;

import jakarta.transaction.Transactional;


@Service
public class AuthServiceImpl implements AuthService {

  private final AccountRepository repository;
  private final PasswordEncoder passwordEncoder;
  private final JwtCore jwtCore;
  private final UserMapper mapper;


  public AuthServiceImpl(
      AccountRepository repository, 
      PasswordEncoder passwordEncoder, 
      JwtCore jwtCore,
      UserMapper mapper
    ) {
    this.repository = repository;
    this.passwordEncoder = passwordEncoder;
    this.jwtCore = jwtCore;
    this.mapper = mapper;
  }


  @Override
  @Transactional
  public AuthResponse register(RegisterRequest request) {
    Account account = new Account();
    account.setLogin(request.login());
    account.setEmail(request.email());
    account.setPassword(passwordEncoder.encode(request.password()));

    User user = new User();
    user.setName(request.userName());

    user.setAccount(account);
    account.setUser(user);

    Account savedAccount = repository.save(account);

    String token = jwtCore.generateToken(savedAccount.getUser().getId());

    UserDto userDto = mapper.toDto(savedAccount.getUser());

    return AuthResponse.builder()
      .token(token)
      .userDto(userDto)
      .build();
  }


  @Override
  public AuthResponse login(LoginRequest request) {
    Account account = repository.findByEmail(request.email())
      .orElseThrow(() -> new IllegalArgumentException("No account with such email: " + request.email()));

    if (!passwordEncoder.matches(request.password(), account.getPassword())) {
      throw new IllegalArgumentException("Wrong password or login!");
    }

    String token = jwtCore.generateToken(account.getUser().getId());

    return AuthResponse.builder() 
      .token(token)
      .userDto(mapper.toDto(account.getUser()))
      .build();
  }
}
