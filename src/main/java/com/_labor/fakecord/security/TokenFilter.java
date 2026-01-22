package com._labor.fakecord.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com._labor.fakecord.services.UserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class TokenFilter extends OncePerRequestFilter {

  private final JwtCore jwtCore;
  private final UserDetailsService userDetailsService;

  public TokenFilter(JwtCore jwtCore, UserDetailsService userDetailsService) {
    this.jwtCore = jwtCore;
    this.userDetailsService = userDetailsService;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    try {
      String jwt = jwtCore.getJwtFromCookies(request);

      if (null != jwt && jwtCore.validateToken(jwt)) {
        String userId = jwtCore.extractUserId(jwt);

        UserDetails details = userDetailsService.loadUserByUserId(userId);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    } catch (Exception e) {
      System.out.println("Cannot set user authentication: {}" + e.getMessage());
    }

    filterChain.doFilter(request, response);
  }
}
