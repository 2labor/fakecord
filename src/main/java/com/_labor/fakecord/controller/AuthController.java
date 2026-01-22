package com._labor.fakecord.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com._labor.fakecord.domain.dto.AuthResponse;
import com._labor.fakecord.domain.dto.LoginRequest;
import com._labor.fakecord.domain.dto.RegisterRequest;
import com._labor.fakecord.domain.entity.RefreshToken;
import com._labor.fakecord.domain.mappper.UserMapper;
import com._labor.fakecord.repository.UserRepository;
import com._labor.fakecord.security.JwtCore;
import com._labor.fakecord.services.AuthService;
import com._labor.fakecord.services.RefreshTokenService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
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

  public AuthController(AuthService service, JwtCore jwtCore, UserMapper userMapper, UserRepository repository, RefreshTokenService refreshTokenService) {
    this.service = service;
    this.jwtCore = jwtCore;
    this.userMapper = userMapper;
    this.userRepository = repository;
    this.refreshTokenService = refreshTokenService; 
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
  public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
    AuthResponse response = service.login(request);

    return generateAuthResponse(response);
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
        String newAccessToken = jwtCore.generateToken(rt.getUser().getId());
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
}
