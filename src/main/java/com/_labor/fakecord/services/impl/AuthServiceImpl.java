package com._labor.fakecord.services.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com._labor.fakecord.domain.dto.AuthResponse;
import com._labor.fakecord.domain.dto.LoginRequest;
import com._labor.fakecord.domain.dto.RegisterRequest;
import com._labor.fakecord.domain.dto.UserDto;
import com._labor.fakecord.domain.dto.VerificationRequest;
import com._labor.fakecord.domain.entity.Account;
import com._labor.fakecord.domain.entity.AuthMethodType;
import com._labor.fakecord.domain.entity.AuthProvider;
import com._labor.fakecord.domain.entity.EmailIdentity;
import com._labor.fakecord.domain.entity.TokenType;
import com._labor.fakecord.domain.entity.User;
import com._labor.fakecord.domain.entity.UserAuthenticator;
import com._labor.fakecord.domain.entity.VerificationToken;
import com._labor.fakecord.domain.mappper.UserMapper;
import com._labor.fakecord.infrastructure.TokenProvider;
import com._labor.fakecord.repository.AccountRepository;
import com._labor.fakecord.repository.UserRepository;
import com._labor.fakecord.security.JwtCore;
import com._labor.fakecord.services.AuthService;
import com._labor.fakecord.services.IdentityService;
import com._labor.fakecord.services.UserAuthenticatorService;
import com._labor.fakecord.services.VerificationTokenService;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

  private final AccountRepository repository;
  private final PasswordEncoder passwordEncoder;
  private final TokenProvider tokenProvider;
  private final UserMapper mapper;
  private final UserRepository userRepository;
  private final UserAuthenticatorService userAuthenticatorService;
  private final VerificationTokenService verificationTokenService;
  private final IdentityService identityService;

  public AuthServiceImpl(
      AccountRepository repository, 
      PasswordEncoder passwordEncoder, 
      TokenProvider tokenProvider,
      UserMapper mapper,
      UserRepository userRepository,
      UserAuthenticatorService userAuthenticatorService,
      VerificationTokenService verificationTokenService,
      IdentityService identityService
    ) {
    this.repository = repository;
    this.passwordEncoder = passwordEncoder;
    this.tokenProvider = tokenProvider;
    this.mapper = mapper;
    this.userRepository = userRepository;
    this.userAuthenticatorService = userAuthenticatorService;
    this.verificationTokenService = verificationTokenService;
    this.identityService = identityService;
  }


  @Override
  @Transactional
  public AuthResponse register(RegisterRequest request) {
    if (identityService.existByEmail(request.email())) {
      throw new IllegalArgumentException("Account with such email is already exist!");
    }

    User user = identityService.findByEmail(request.email())
    .map(EmailIdentity::getUser)
    .orElseGet(() -> {
      User newUser = new User();
      newUser.setName(request.userName());
      return userRepository.save(newUser);
    });

    identityService.linkEmailToUser(user, request.email(), AuthProvider.LOCAL, false, true);

    Account account = new Account();
    account.setLogin(request.login());
    account.setPassword(passwordEncoder.encode(request.password()));
    user.setAccount(account);
    account.setUser(user);
    Account savedAccount = repository.save(account);

    String token = tokenProvider.createAccessToken(user.getId());

    UserDto userDto = mapper.toDto(savedAccount.getUser());

    return AuthResponse.builder()
      .token(token)
      .userDto(userDto)
      .build();
  }


  @Override
  public AuthResponse login(LoginRequest request, String ip, String agent) {
    User user = findUserByIdentifier(request.identifier());

    Account account = repository.findByUser(user)
      .orElseThrow(() -> new IllegalArgumentException("Password not set for this account. Use Social Login."));

    if (!passwordEncoder.matches(request.password(), account.getPassword())) {
      throw new IllegalArgumentException("Wrong password or login!");
    }

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

    String token = tokenProvider.createAccessToken(user.getId());
    return AuthResponse.builder()
      .token(token)
      .userDto(mapper.toDto(user))
      .mfaRequired(false)
      .build();
  }

  private User findUserByIdentifier(String identifier) {
    return identityService.findByEmail(identifier)
      .map(EmailIdentity::getUser)
      .orElseGet(() -> repository.findByLogin(identifier)
      .map(Account::getUser)
    .orElseThrow(() -> new IllegalArgumentException("Invalid credential")));
  }

  @Override
  public AuthResponse verify(VerificationRequest request, String ip, String agent) {
    VerificationToken mfaSession = verificationTokenService.verifyToken(request.tokenId(), request.type(), ip, agent)
      .orElseThrow(() -> new IllegalArgumentException("Verification session expired or invalid"));
    
    User user = mfaSession.getUser();

    boolean isValid = userAuthenticatorService.verifyCode(user, request.authType(), request.code());

    if (!isValid) {
      verificationTokenService.recordFailedAttempt(request.tokenId());
      throw new IllegalArgumentException("Invalid verify code!");
    }

    String jwtToken = tokenProvider.createAccessToken(user.getId());

    return AuthResponse.builder()
      .token(jwtToken)
      .userDto(mapper.toDto(user))
      .mfaRequired(false)
      .build();
  }


  @Override
  @Transactional
  public void logoutEverywhere(UUID userId) {
    userRepository.incrementTokenVersion(userId);

    tokenProvider.removeAllAccess(userId); 
    
    log.info("Global logout for user {}: Database version incremented and all sessions revoked.", userId);
  }
}