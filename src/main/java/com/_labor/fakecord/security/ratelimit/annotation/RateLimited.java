package com._labor.fakecord.security.ratelimit.annotation;

import java.lang.annotation.*;

import com._labor.fakecord.security.ratelimit.RateLimitSource;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimited {
  String key() default "default";
  int capacity() default 3;
  int refillSeconds() default 60;
  RateLimitSource source() default RateLimitSource.IP;
}