package com._labor.fakecord.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com._labor.fakecord.domain.dto.AccountDto;
import com._labor.fakecord.domain.dto.AuthResponse;
import com._labor.fakecord.domain.dto.LoginRequest;
import com._labor.fakecord.domain.dto.RegisterRequest;
import com._labor.fakecord.domain.entity.Account;
import com._labor.fakecord.domain.mappper.AccountMapper;
import com._labor.fakecord.repository.AccountRepository;
import com._labor.fakecord.security.JwtCore;
import com._labor.fakecord.services.AuthService;

import jakarta.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("api/auth")
public class AuthController {

  private final AccountMapper accountMapper;
  private final AuthService service;
  private final JwtCore jwtCore;
  private final AccountRepository repository;

  public AuthController(AuthService service, JwtCore jwtCore, AccountMapper accountMapper, AccountRepository repository) {
    this.service = service;
    this.jwtCore = jwtCore;
    this.accountMapper = accountMapper;
    this.repository = repository;
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
    AuthResponse response = service.register(request);

    ResponseCookie cookie = jwtCore.createCookie(response.token());

    return ResponseEntity.ok()
      .header(HttpHeaders.SET_COOKIE, cookie.toString())
      .body(response.accountDto());
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
    AuthResponse response = service.login(request);

    ResponseCookie cookie = jwtCore.createCookie(response.token());

    return ResponseEntity.ok()
      .header(HttpHeaders.SET_COOKIE, cookie.toString())
      .body(response.accountDto());
  }

  @PostMapping("/logout")
  public ResponseEntity<?> logout() {
    ResponseCookie cookie = jwtCore.deleteJwtCookie();
    
    return ResponseEntity.ok()
      .header(HttpHeaders.SET_COOKIE, cookie.toString())
      .body("Log out successful!");
  }

  @GetMapping("/me")
  public ResponseEntity<?> getMe() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated() 
        || authentication instanceof AnonymousAuthenticationToken) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
    }

    String login = authentication.getName();
 
    return repository.findByLogin(login)
      .map(account -> {
          AccountDto dto = accountMapper.toDto(account);
          return ResponseEntity.ok(dto);
      })
      .map(ResponseEntity.class::cast)
      .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found!"));
  }
}
