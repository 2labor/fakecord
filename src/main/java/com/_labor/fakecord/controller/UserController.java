package com._labor.fakecord.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com._labor.fakecord.domain.dto.UserDto;
import com._labor.fakecord.services.UserServices;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/users")
public class UserController {
  private final UserServices services;

  public UserController(UserServices services) {
    this.services = services;
  }
  
  @PostMapping("/register")
  public ResponseEntity<UserDto> registerUser(@Valid @RequestBody UserDto dto) {
    UserDto createdUser = services.createUser(dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
  }
  
}
