package com._labor.fakecord.controller;

import java.nio.file.attribute.UserPrincipal;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com._labor.fakecord.security.ratelimit.RateLimitSource;
import com._labor.fakecord.security.ratelimit.annotation.RateLimited;
import com._labor.fakecord.services.EmailVerificationService;

@RestController
@RequestMapping("/api/auth/verify")
public class VerificationController {
  
  private final EmailVerificationService service;

  public VerificationController(EmailVerificationService service) {
    this.service = service;
  }

  @GetMapping("/email")
  public ResponseEntity<?> confirm(@RequestParam String token) {
    service.confirmEmail(token);
    return ResponseEntity.ok("Email was confirm successfully!");
  }

  @RateLimited(key = "resend_email", capacity = 1, refillSeconds = 60, source = RateLimitSource.JSON_BODY)
  @PostMapping("/resend")
  public ResponseEntity<?> resend(Authentication authentication) {
    UUID userId = UUID.fromString(authentication.getName());

    service.resendToPrimaryEmail(userId);
    return ResponseEntity.ok("Verification link resent to your primary email.");
  }

}
