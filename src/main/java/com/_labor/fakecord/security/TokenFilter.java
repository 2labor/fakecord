package com._labor.fakecord.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com._labor.fakecord.controller.AuthController;
import com._labor.fakecord.services.UserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class TokenFilter extends OncePerRequestFilter {

    private final AuthController authController;
  private final JwtCore jwtCore;
  private final UserDetailsService userDetailsService;

  public TokenFilter(JwtCore jwtCore, UserDetailsService userDetailsService, AuthController authController) {
    this.jwtCore = jwtCore;
    this.userDetailsService = userDetailsService;
    this.authController = authController;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    String authHeader = request.getHeader("Authorization");
    String username = null;
    String jwt = null;

    if (null != authHeader && authHeader.startsWith("Bearer ")) {
      jwt = authHeader.substring(7);
      username = jwtCore.extractUsername(jwt);
    }
    
    if (null != username && SecurityContextHolder.getContext().getAuthentication() == null) {
      UserDetails userDetails = userDetailsService.loadUserByUsername(username);
      UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
      SecurityContextHolder.getContext().setAuthentication(auth); 
    }

    filterChain.doFilter(request, response);
  }
}
