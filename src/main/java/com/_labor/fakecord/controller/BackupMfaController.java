package com._labor.fakecord.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com._labor.fakecord.domain.dto.VerificationRequest;
import com._labor.fakecord.domain.entity.User;
import com._labor.fakecord.domain.entity.VerificationToken;
import com._labor.fakecord.domain.mappper.UserMapper;
import com._labor.fakecord.security.JwtCore;
import com._labor.fakecord.services.AuthService;
import com._labor.fakecord.services.BackupCodeService;
import com._labor.fakecord.services.RefreshTokenService;
import com._labor.fakecord.services.VerificationTokenService;
import com._labor.fakecord.utils.RequestUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("api/auth/mfa/backup")
public class BackupMfaController {
  private final BackupCodeService backupCodeService;
  private final VerificationTokenService verificationTokenService;
  private final AuthService authService;
  private final JwtCore jwtCore;
  private final RefreshTokenService refreshTokenService;
  private final UserMapper userMapper;

  public BackupMfaController(
    BackupCodeService backupCodeService,
    VerificationTokenService verificationTokenService,
    AuthService authService,
    JwtCore jwtCore,
    RefreshTokenService refreshTokenService,
    UserMapper userMapper
  ) {
    this.backupCodeService = backupCodeService;
    this.verificationTokenService = verificationTokenService;
    this.authService = authService;
    this.jwtCore = jwtCore;
    this.refreshTokenService = refreshTokenService;
    this.userMapper = userMapper;
  }

  @PostMapping("/verify")
  public ResponseEntity<?> verifyBackupCode(
    @Valid @RequestBody VerificationRequest request, 
    HttpServletRequest httpRequest
  ) {
    String ip = RequestUtil.getClientIp(httpRequest);
    String agent = RequestUtil.getClientAgent(httpRequest);

    VerificationToken mfaSession = verificationTokenService.verifyToken(
      request.tokenId(),
      request.type(),
      ip,
      agent
    )
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired session"));

    User user = mfaSession.getUser();

    boolean isValid = backupCodeService.verifyAndUseCode(user, request.code(), httpRequest);

    if (!isValid) {
      verificationTokenService.recordFailedAttempt(request.tokenId());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid backup code");
    }

    return generateAuthResponse(user);
  } 


  private ResponseEntity<?> generateAuthResponse(User user) {
    String accessToken = jwtCore.generateToken(user.getId(), user.getTokenVersion());
    ResponseCookie accessCookie = jwtCore.createAccessTokenCookie(accessToken);

    var refreshToken = refreshTokenService.createRefreshToken(user.getId());
    ResponseCookie refreshCookie = jwtCore.createRefreshTokenCookie(refreshToken.getToken());

    return ResponseEntity.ok()
      .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
      .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
      .body(userMapper.toDto(user));
  }
}
