package com._labor.fakecord.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com._labor.fakecord.domain.dto.PasswordResetConfirm;
import com._labor.fakecord.domain.dto.PasswordResetRequest;
import com._labor.fakecord.services.PasswordResetService;
import com._labor.fakecord.utils.RequestUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth/password")
public class PasswordResetController {
  private final PasswordResetService service;

  public PasswordResetController(PasswordResetService service) {
    this.service = service;
  }

  @PostMapping("/reset-request")
  public ResponseEntity<?> requestRest(
    @Valid @RequestBody PasswordResetRequest request,
    HttpServletRequest httpRequest
  ) {
    String ip = RequestUtil.getClientIp(httpRequest);
    String agent = RequestUtil.getClientAgent(httpRequest);

    service.initiateReset(request.email(), ip, agent);

    return ResponseEntity.ok(
      Map.of(
      "message", "If an account with this email exists, a reset link has been sent."
      ));
  }

  @PostMapping("/reset-confirm")
  public ResponseEntity<?> confirmReset(
    @Valid @RequestBody PasswordResetConfirm confirm,
    HttpServletRequest request
  ) {
    String ip = RequestUtil.getClientIp(request);
    String agent = RequestUtil.getClientAgent(request);

    service.completeReset(
      confirm.token(),
      confirm.newPassword(),
      ip,
      agent
    );

    return ResponseEntity.ok(
      Map.of("message", "Password has been successfully updated.")
    );
  }
}
