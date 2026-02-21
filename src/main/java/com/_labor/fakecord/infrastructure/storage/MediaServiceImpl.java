package com._labor.fakecord.infrastructure.storage;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.method.P;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.stereotype.Service;

import com._labor.fakecord.domain.entity.UserProfile;
import com._labor.fakecord.domain.enums.ImageType;
import com._labor.fakecord.domain.enums.MediaType;
import com._labor.fakecord.repository.UserProfileRepository;
import com._labor.fakecord.repository.UserRepository;
import com._labor.fakecord.services.UserProfileCache;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MediaServiceImpl implements MediaService {

  private final FileStorageService fileStorageService;
  private final UserProfileRepository profileRepository;
  private final UserProfileCache cache;

  private static final long MAX_AVATAR_SIZE = 2 * 1024 * 1024; 
  private static final long MAX_BANNER_SIZE = 5 * 1024 * 1024;

  @Value("${app.s3.endpoint}")
    private String s3Endpoint;

  @Value("${app.s3.bucket-name}")
  private String bucketName;

  public MediaServiceImpl(
    FileStorageService fileStorageService,
    UserProfileRepository profileRepository,
    UserProfileCache cache
  ) {
    this.fileStorageService = fileStorageService;
    this.profileRepository = profileRepository;
    this.cache = cache;
  }

  @Override
  @Transactional
  public String getAvatarUploadUrl(UUID userId, ImageType type) {
    return processUploadUrl(userId, type, MediaType.AVATAR);
  }

  @Override
  @Transactional
  public String getBannerUploadUrl(UUID userId, ImageType type) {
    return processUploadUrl(userId, type, MediaType.BANNER);
  }

  private String processUploadUrl(UUID userId, ImageType imageType, MediaType mediaType) {

    long limit = (mediaType == MediaType.AVATAR) ? MAX_AVATAR_SIZE : MAX_BANNER_SIZE;

    log.info("Processing {} upload for user: {}", mediaType, userId);

    UserProfile profile = profileRepository.findById(userId)
      .orElseThrow(() -> new RuntimeException("Profile not found"));

    String objectPath = mediaType.getFolder() + "/" + userId;

    String uploadUrl = fileStorageService.generateUploadUrl(objectPath, imageType.getMimeType(), limit);

    String publicUrl = String.format("%s/%s/%s", s3Endpoint, bucketName, objectPath);

    if (mediaType == MediaType.AVATAR) {
      profile.setAvatarUrl(publicUrl);
    } else if (mediaType == MediaType.BANNER) {
      profile.setBannerUrl(publicUrl);
    }

    profileRepository.save(profile);

    cache.evict(userId);

    return uploadUrl;
  }  
}
