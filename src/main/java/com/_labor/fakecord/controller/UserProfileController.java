package com._labor.fakecord.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com._labor.fakecord.domain.dto.UserProfileFullDto;
import com._labor.fakecord.domain.dto.UserProfileUpdateDto;
import com._labor.fakecord.services.UserProfileServices;

import jakarta.validation.Valid;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/profiles")
public class UserProfileController {
  private final UserProfileServices service;

  public UserProfileController(UserProfileServices service) {
    this.service = service;
  }

  @GetMapping("/{userId}")
  public ResponseEntity<UserProfileFullDto> getProfile(@PathVariable UUID userId) {
    return ResponseEntity.ok(service.getById(userId));
  }

  @PatchMapping("/{userId}")
  public ResponseEntity<UserProfileFullDto> updateProfile(
    @PathVariable UUID userId,
    @Valid @RequestBody UserProfileUpdateDto updateDto
  ) {
    return ResponseEntity.ok(service.update(userId, updateDto));
  }
}
