package com._labor.fakecord.services.impl;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com._labor.fakecord.domain.dto.AuthResponse;
import com._labor.fakecord.domain.dto.LoginRequest;
import com._labor.fakecord.domain.dto.RegisterRequest;
import com._labor.fakecord.domain.dto.UserDto;
import com._labor.fakecord.domain.dto.VerificationRequest;
import com._labor.fakecord.domain.entity.Account;
import com._labor.fakecord.domain.entity.AuthMethodType;
import com._labor.fakecord.domain.entity.TokenType;
import com._labor.fakecord.domain.entity.User;
import com._labor.fakecord.domain.entity.UserAuthenticator;
import com._labor.fakecord.domain.entity.VerificationToken;
import com._labor.fakecord.domain.mappper.UserMapper;
import com._labor.fakecord.repository.AccountRepository;
import com._labor.fakecord.repository.UserRepository;
import com._labor.fakecord.security.JwtCore;
import com._labor.fakecord.services.AuthService;
import com._labor.fakecord.services.UserAuthenticatorService;
import com._labor.fakecord.services.VerificationTokenService;

import jakarta.transaction.Transactional;


@Service
public class AuthServiceImpl implements AuthService {

  private final AccountRepository repository;
  private final PasswordEncoder passwordEncoder;
  private final JwtCore jwtCore;
  private final UserMapper mapper;
  private final UserRepository userRepository;
  private final UserAuthenticatorService userAuthenticatorService;
  private final VerificationTokenService verificationTokenService;

  public AuthServiceImpl(
      AccountRepository repository, 
      PasswordEncoder passwordEncoder, 
      JwtCore jwtCore,
      UserMapper mapper,
      UserRepository userRepository,
      UserAuthenticatorService userAuthenticatorService,
      VerificationTokenService verificationTokenService
    ) {
    this.repository = repository;
    this.passwordEncoder = passwordEncoder;
    this.jwtCore = jwtCore;
    this.mapper = mapper;
    this.userRepository = userRepository;
    this.userAuthenticatorService = userAuthenticatorService;
    this.verificationTokenService = verificationTokenService;
  }


  @Override
  @Transactional
  public AuthResponse register(RegisterRequest request) {
    if (repository.existsByEmail(request.email())) {
      throw new IllegalArgumentException("Account with such email is already exist!");
    }

    User user = userRepository.findByEmailAcrossAllProviders(request.email())
    .orElseGet(() -> {
      User newUser = new User();
      newUser.setName(request.userName());
      return userRepository.save(newUser);
    });

    Account account = new Account();
    account.setLogin(request.login());
    account.setEmail(request.email());
    account.setPassword(passwordEncoder.encode(request.password()));

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
  public AuthResponse login(LoginRequest request, String ip, String agent) {
    Account account = repository.findByEmail(request.email())
      .orElseThrow(() -> new IllegalArgumentException("No account with such email: " + request.email()));

    if (!passwordEncoder.matches(request.password(), account.getPassword())) {
      throw new IllegalArgumentException("Wrong password or login!");
    }

    User user = account.getUser();
    var activeMfaMethods = userAuthenticatorService.getActiveMethods(user.getId());

    if (!activeMfaMethods.isEmpty()) {
      var mfaSession = verificationTokenService.createToken(user, TokenType.MFA_SESSION, ip, agent);

      List<AuthMethodType> methodTypes = activeMfaMethods.stream()
        .map(UserAuthenticator::getType)
        .toList();

      return AuthResponse.builder()
        .mfaRequired(true)
        .sessionId(mfaSession.getId().toString())
        .availableMethods(methodTypes)
        .build();
    }

    String token = jwtCore.generateToken(user.getId());
    return AuthResponse.builder()
      .token(token)
      .userDto(mapper.toDto(user))
      .mfaRequired(false)
      .build();
  }


  @Override
  public AuthResponse verify(VerificationRequest request, String ip, String agent) {
    VerificationToken mfaSession = verificationTokenService.verifyToken(request.tokenId(), request.type(), ip, agent)
      .orElseThrow(() -> new IllegalArgumentException("Verification session expired or invalid"));
    
    User user = mfaSession.getUser();

    boolean isValid = userAuthenticatorService.verifyCode(user, request.authType(), request.code());

    if (!isValid) {
      throw new IllegalArgumentException("Invalid verify code!");
    }

    String jwtToken = jwtCore.generateToken(user.getId());

    return AuthResponse.builder()
      .token(jwtToken)
      .userDto(mapper.toDto(user))
      .mfaRequired(false)
      .build();
  }
}