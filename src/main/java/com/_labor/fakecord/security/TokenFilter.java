package com._labor.fakecord.security;

import java.io.IOException;
import java.util.UUID;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com._labor.fakecord.security.versions.TokenVersionManager;
import com._labor.fakecord.services.UserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TokenFilter extends OncePerRequestFilter {

  private final JwtCore jwtCore;
  private final UserDetailsService userDetailsService;
  private final TokenVersionManager versionManager;

  public TokenFilter(JwtCore jwtCore, UserDetailsService userDetailsService, TokenVersionManager versionManager) {
    this.jwtCore = jwtCore;
    this.userDetailsService = userDetailsService;
    this.versionManager = versionManager;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    try {
      String jwt = jwtCore.getJwtFromCookies(request);

      if (null != jwt && jwtCore.validateToken(jwt)) {
        String userStringId = jwtCore.extractUserId(jwt);
        Integer tokenVersion = jwtCore.extractTokenVersion(jwt);
        UUID userId = UUID.fromString(userStringId); 

        int currentVersion = versionManager.getCurrentVersion(userId);

        if (null != tokenVersion && currentVersion == tokenVersion) {
          UserDetails details = userDetailsService.loadUserByUserId(userStringId);
          UsernamePasswordAuthenticationToken authentication =
             new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());
          authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
          log.warn("Rejected stale token for user {}. Token version: {}, Current version: {}", 
            userId, tokenVersion, currentVersion);
        }
      }
    } catch (Exception e) {
      System.out.println("Cannot set user authentication: {}" + e.getMessage());
    }

    filterChain.doFilter(request, response);
  }
}
