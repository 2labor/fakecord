package com._labor.fakecord.controller;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com._labor.fakecord.domain.enums.ImageType;
import com._labor.fakecord.infrastructure.storage.MediaService;

@RestController
@RequestMapping("/api/media")
public class MediaController {
  
  private final MediaService mediaService;

  public MediaController(MediaService mediaService) { 
    this.mediaService = mediaService;
  }


  @PostMapping("/avatar")
  public ResponseEntity<Map<String, String>> getAvatarUrl(
    @AuthenticationPrincipal UUID userId,
    @RequestParam ImageType type
  ) {
    String uploadUrl = mediaService.getAvatarUploadUrl(userId, type);

    return ResponseEntity.ok(Map.of("uploadUrl", uploadUrl));
  }

  @PostMapping("/banner")
  public ResponseEntity<Map<String, String>> getBannerUrl(
      @AuthenticationPrincipal UUID userId,
      @RequestParam ImageType type
  ) {
    String uploadUrl = mediaService.getBannerUploadUrl(userId, type);
    
    return ResponseEntity.ok(Map.of("uploadUrl", uploadUrl));
  }
}
