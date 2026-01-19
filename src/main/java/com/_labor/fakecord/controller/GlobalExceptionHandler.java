package com._labor.fakecord.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException; 
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
}