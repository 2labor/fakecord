package com._labor.fakecord.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException; 
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com._labor.fakecord.security.ratelimit.exception.RateLimitExceededException;

@RestControllerAdvice
public class GlobalExceptionHandler {
  
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<String> handelIllegalArgumentException(IllegalArgumentException ex) {
    return ResponseEntity.badRequest().body(ex.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException ex) {
    String message = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
    return ResponseEntity.badRequest().body(message); 
  }

  @ExceptionHandler(Exception.class) 
  public ResponseEntity<String> handelUnexpectedException(Exception ex) {
    ex.printStackTrace();
    return ResponseEntity.internalServerError().body("An unexpected error occurred: " + ex.getMessage());
  } 

  @ExceptionHandler(RateLimitExceededException.class)
  public ResponseEntity<Map<String, Object>> handleRateLimit(RateLimitExceededException ex) {
    Map<String, Object> response = new HashMap<>();
      response.put("status", 429);
      response.put("error", "Too Many Requests");
      response.put("message", ex.getMessage());
    
    return ResponseEntity.status(429).body(response);
  }
}