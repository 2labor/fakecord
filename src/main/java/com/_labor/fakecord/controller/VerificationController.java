package com._labor.fakecord.controller;

import java.nio.file.attribute.UserPrincipal;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com._labor.fakecord.services.EmailVerificationService;

@RestController
@RequestMapping("/api/auth/verify")
public class VerificationController {
  
  private final EmailVerificationService service;

  public VerificationController(EmailVerificationService service) {
    this.service = service;
  }

  @PostMapping("/email")
  public ResponseEntity<?> confirm(@RequestParam String token) {
    service.confirmEmail(token);
    return ResponseEntity.ok("Email was confirm successfully!");
  }

  @PostMapping("/resend")
  public ResponseEntity<?> resend(Authentication authentication) {
    UUID userId = UUID.fromString(authentication.getName());

    service.resendToPrimaryEmail(userId);
    return ResponseEntity.ok("Verification link resent to your primary email.");
  }

}
