package com._labor.fakecord.security.ratelimit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.TOO_MANY_REQUESTS)
public class RateLimitExceededException extends RuntimeException{
  public RateLimitExceededException(String message) {
    super(message);
  }
}
