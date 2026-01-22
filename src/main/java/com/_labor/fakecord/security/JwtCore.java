package com._labor.fakecord.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtCore {
  @Value("${fakecord.jwt.secret}")
  private String secret;

  @Value("${fakecord.jwt.lifetime}")
  private int lifetime;

  @Value("${fakecord.jwt.refreshExpirationMs}")
  private long refreshTokenDurationMs;

  private SecretKey getSigningKey() {
    return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
  }

  public String generateToken(UUID userId) {
    return Jwts.builder()
      .setSubject(userId.toString()) // set user id as a subject
      .setIssuedAt(new Date()) // date of creation
      .setExpiration(new Date(System.currentTimeMillis() + lifetime)) // date of expiring
      .signWith(getSigningKey(),SignatureAlgorithm.HS256) // sign up key
      .compact(); // build final string
  } 

  public String extractUserId(String token) {
    return Jwts.parserBuilder()
      .setSigningKey(getSigningKey())
      .build()
      .parseClaimsJws(token)
      .getBody()
      .getSubject();
  }

  public String getJwtFromCookies(HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if ("access-token".equals(cookie.getName())){
          return cookie.getValue();
        }
      }
    }
    return null;
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      System.out.println("JWT error: " + e.getMessage());
      return false;
    }
  }

  public ResponseCookie createAccessTokenCookie(String token) {
    return ResponseCookie.from("access-token", token)
      .httpOnly(true)
      .secure(false)
      .path("/")
      .maxAge(3600)
      .sameSite("Lax")
      .build();
  }

  public ResponseCookie createRefreshTokenCookie(String refreshToken) {
    return ResponseCookie.from("refresh-token", refreshToken)
      .httpOnly(true)
      .secure(false)
      .path("/")
      .maxAge(refreshTokenDurationMs / 1000)
      .sameSite("Lax")
      .build();
  }

  public ResponseCookie deleteAccessTokenCookie() {
    return ResponseCookie.from("access-token", "")
      .httpOnly(true)
      .path("/")
      .maxAge(0)
      .build();
  }

  public ResponseCookie deleteRefreshTokenCookie() {
    return ResponseCookie.from("refresh-token", "")
      .httpOnly(true)
      .path("/api/auth/refresh")
      .maxAge(0)
      .build();
  }
}
