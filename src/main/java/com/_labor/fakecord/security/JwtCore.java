package com._labor.fakecord.security;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtCore {
  @Value("${fakecord.jwt.secret}")
  private String secret;

  @Value("${fakecord.jwt.lifetime}")
  private int lifetime;

  private Key getSigningKey() {
    return Keys.hmacShaKeyFor(secret.getBytes());
  }

  public String generateToken(String login) {
    return Jwts.builder()
      .setSubject(login) // set login as a subject
      .setIssuedAt(new Date()) // date of creation
      .setExpiration(new Date(System.currentTimeMillis() + lifetime)) // date of expiring
      .signWith(getSigningKey(),  SignatureAlgorithm.HS256) // sign up key
      .compact(); // build final string
  } 

  public String extractUsername(String token) {
    return Jwts.parserBuilder()
      .setSigningKey(getSigningKey())
      .build()
      .parseClaimsJws(token)
      .getBody()
      .getSubject();
  }
}
