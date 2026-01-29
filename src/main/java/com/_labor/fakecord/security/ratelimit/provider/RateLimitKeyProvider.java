package com._labor.fakecord.security.ratelimit.provider;

import com._labor.fakecord.security.ratelimit.annotation.RateLimited;

import jakarta.servlet.http.HttpServletRequest;

public interface RateLimitKeyProvider {
  String generateKey(HttpServletRequest request, RateLimited annotation);
}
 