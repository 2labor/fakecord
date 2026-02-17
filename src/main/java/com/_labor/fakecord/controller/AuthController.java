package com._labor.fakecord.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com._labor.fakecord.domain.dto.AuthResponse;
import com._labor.fakecord.domain.dto.LoginRequest;
import com._labor.fakecord.domain.dto.MfaEnableResponse;
import com._labor.fakecord.domain.dto.MfaRegistrationResponse;
import com._labor.fakecord.domain.dto.MfaSetupRequest;
import com._labor.fakecord.domain.dto.RegisterRequest;
import com._labor.fakecord.domain.dto.VerificationRequest;
import com._labor.fakecord.domain.entity.AuthMethodType;
import com._labor.fakecord.domain.entity.RefreshToken;
import com._labor.fakecord.domain.entity.User;
import com._labor.fakecord.domain.entity.UserAuthenticator;
import com._labor.fakecord.domain.mappper.UserMapper;
import com._labor.fakecord.repository.UserRepository;
import com._labor.fakecord.security.JwtCore;
import com._labor.fakecord.security.ratelimit.RateLimitSource;
import com._labor.fakecord.security.ratelimit.annotation.RateLimited;
import com._labor.fakecord.services.AuthService;
import com._labor.fakecord.services.BackupCodeService;
import com._labor.fakecord.services.RefreshTokenService;
import com._labor.fakecord.services.UserAuthenticatorService;
import com._labor.fakecord.utils.RequestUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("api/auth")
public class AuthController {

  private final UserMapper userMapper;
  private final AuthService service;
  private final JwtCore jwtCore;
  private final UserRepository userRepository;
  private final RefreshTokenService refreshTokenService;
  private final UserAuthenticatorService userAuthenticatorService;
  private final BackupCodeService backupCodeService;

  public AuthController(
    AuthService service, 
    JwtCore jwtCore, 
    UserMapper userMapper, 
    UserRepository repository, 
    RefreshTokenService refreshTokenService,
    UserAuthenticatorService userAuthenticatorService,
    BackupCodeService backupCodeService
  ) {
    this.service = service;
    this.jwtCore = jwtCore;
    this.userMapper = userMapper;
    this.userRepository = repository;
    this.refreshTokenService = refreshTokenService; 
    this.userAuthenticatorService = userAuthenticatorService;
    this.backupCodeService = backupCodeService;
  }

  private ResponseEntity<?> generateAuthResponse(AuthResponse response) {
    ResponseCookie accessCookie = jwtCore.createAccessTokenCookie(response.token());

    RefreshToken refreshToken = refreshTokenService.createRefreshToken(response.userDto().id());

    ResponseCookie refreshCookie = jwtCore.createRefreshTokenCookie(refreshToken.getToken());

    return ResponseEntity.ok()
      .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
      .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
      .body(response.userDto());
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
    AuthResponse response = service.register(request);

    return generateAuthResponse(response);
  }

  @PostMapping("/login")
  @RateLimited(
    key = "auth_login",
    capacity = 5,
    refillSeconds = 300,
    source = RateLimitSource.JSON_BODY
  )
  public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
    String ip = RequestUtil.getClientIp(httpRequest);
    String agent = RequestUtil.getClientAgent(httpRequest);

    AuthResponse authResponse = service.login(request, ip, agent);

    if (authResponse.mfaRequired()) {
      return ResponseEntity.ok(authResponse);
    }

    return generateAuthResponse(authResponse);
  }

  @PostMapping("/verify")
  public ResponseEntity<?> verify(@Valid @RequestBody VerificationRequest request, HttpServletRequest httpRequest) {  
    String ip = RequestUtil.getClientIp(httpRequest);
    String agent = RequestUtil.getClientAgent(httpRequest);

    AuthResponse authResponse = service.verify(request, ip, agent);

    return generateAuthResponse(authResponse);
  }

  @PostMapping("/logout")
  public ResponseEntity<?> logout() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null && !(auth instanceof AnonymousAuthenticationToken)) {
      UUID userId = UUID.fromString(auth.getName());
      refreshTokenService.deleteByUserId(userId);
    }
    
    ResponseCookie deleteAccessCookie = jwtCore.deleteAccessTokenCookie();
    ResponseCookie deleteRefreshCookie = jwtCore.deleteRefreshTokenCookie();

    return ResponseEntity.ok()
      .header(HttpHeaders.SET_COOKIE, deleteAccessCookie.toString())
      .header(HttpHeaders.SET_COOKIE, deleteRefreshCookie.toString())
      .body("Log out successfully!");
  }

  @PostMapping("/refresh")
  public ResponseEntity<?> refresh(HttpServletRequest request) {
    String token = null;
    Cookie[] cookies = request.getCookies();
    if (null != cookies) {
      for (Cookie cookie : cookies) {
        if ("refresh-token".equals(cookie.getName())) {
          token = cookie.getValue();          
        }
      }
    }

    if (token == null || token.isEmpty()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token is missing");
    }

    return refreshTokenService.findByToken(token)
      .map(refreshTokenService::verifyExpiration)
      .map(rt -> {
        String newAccessToken = jwtCore.generateToken(
          rt.getUser().getId(),
          rt.getUser().getTokenVersion()
        );
        ResponseCookie accessCookie = jwtCore.createAccessTokenCookie(newAccessToken);
        ResponseCookie refreshCookie = jwtCore.createRefreshTokenCookie(rt.getToken());

        return ResponseEntity.ok()
          .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
          .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
          .body(userMapper.toDto(rt.getUser()));
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.FORBIDDEN).build());
  }
  

  @GetMapping("/me")
  public ResponseEntity<?> getMe() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated() 
        || authentication instanceof AnonymousAuthenticationToken) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
    }

    UUID userId = UUID.fromString(authentication.getName());
    
    return userRepository.findById(userId)
      .<ResponseEntity<?>>map(user -> ResponseEntity.ok(userMapper.toDto(user)))
      .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found!"));
  }

  @GetMapping("/mfa/status")
public ResponseEntity<?> getMfaStatus() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
    }

    try {
        UUID userId = UUID.fromString(auth.getName());
        
        List<AuthMethodType> activeTypes = userAuthenticatorService.getActiveMethods(userId)
                .stream()
                .map(UserAuthenticator::getType)
                .toList();

        return ResponseEntity.ok(activeTypes);
    } catch (Exception e) {
        e.printStackTrace(); 
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }
}

  @GetMapping("/mfa/setup")
  public ResponseEntity<MfaRegistrationResponse> setupMfa() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    UUID userId = UUID.fromString(auth.getName());
    User user = userRepository.findById(userId)
      .orElseThrow(() -> new IllegalArgumentException("User not found!"));
    
    MfaRegistrationResponse response = userAuthenticatorService.initiateMfaSetup(user);

    return ResponseEntity.ok(response);
  }

  @PostMapping("/mfa/enable")
  public ResponseEntity<?> enableMfa(@Valid @RequestBody MfaSetupRequest setupRequest, HttpServletRequest httpRequest) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    UUID userId = UUID.fromString(auth.getName());
    User user = userRepository.findById(userId)
      .orElseThrow(() -> new IllegalArgumentException("User not found!"));
    
    boolean isValid = userAuthenticatorService.verifyTotp(setupRequest.secret(), setupRequest.code());
    
    if (!isValid) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid verification code. Try again.");
    }
  
    userAuthenticatorService.enableMethod(user, AuthMethodType.TOTP, setupRequest.secret());

    List<String> backupCodes = backupCodeService.generateNewCodes(user, httpRequest);

    return ResponseEntity.ok(new MfaEnableResponse(
        "MFA enabled successfully!",
        backupCodes
    ));
  }

  @PostMapping("/mfa/disable")
  public ResponseEntity<String> disableMfa(@Valid @RequestBody VerificationRequest request) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    UUID userId = UUID.fromString(auth.getName());
    
    boolean isValid = userAuthenticatorService.verifyCode(
        userRepository.findById(userId).get(), 
        AuthMethodType.TOTP, 
        request.code()
    );

    if (!isValid) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid code");
    }

    userAuthenticatorService.disableMethod(userId, AuthMethodType.TOTP);
    return ResponseEntity.ok("MFA disabled");
  }

  @PostMapping("/logout/everywhere")
  public ResponseEntity<?> logoutEverywhere() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    if (null == auth || auth instanceof AnonymousAuthenticationToken) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
    }

    UUID userId = UUID.fromString(auth.getName());
    
    service.logoutEverywhere(userId);

    ResponseCookie deleteAccess = jwtCore.deleteAccessTokenCookie();
    ResponseCookie deleteRefresh = jwtCore.deleteRefreshTokenCookie();

    return ResponseEntity.ok()
      .header(HttpHeaders.SET_COOKIE, deleteAccess.toString())
      .header(HttpHeaders.SET_COOKIE, deleteRefresh.toString())
      .body("Logged out from all devices successfully. All existing tokens are now invalid.");
  }
}