package com._labor.fakecord.security.ratelimit.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com._labor.fakecord.security.ratelimit.annotation.RateLimited;
import com._labor.fakecord.security.ratelimit.exception.RateLimitExceededException;
import com._labor.fakecord.security.ratelimit.provider.RateLimitKeyProvider;
import com._labor.fakecord.security.ratelimit.service.RateLimiterService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
public class RateLimitingAspect {

  private final RateLimiterService service;
  private final RateLimitKeyProvider keyProvider;

  public RateLimitingAspect(RateLimitKeyProvider keyProvider, RateLimiterService service) {
    this.keyProvider = keyProvider;
    this.service = service;
  }

  @Around("@annotation(rateLimited)")
  public Object handleRateLimiting(ProceedingJoinPoint joinPoint, RateLimited rateLimited) throws Throwable {
    ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (attr == null) return joinPoint.proceed();
    HttpServletRequest  request = attr.getRequest();

    String key = keyProvider.generateKey(request, rateLimited);

    boolean allowed = service.tryConsume(key, rateLimited.capacity(), rateLimited.refillSeconds());

    if (!allowed) {
      log.warn("Rate limit exceeded for key: {}", key);
      throw new RateLimitExceededException("Too many requests. Try again later.");
    }

    return joinPoint.proceed();
  }
}
