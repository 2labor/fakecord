package com._labor.fakecord.infrastructure.storage;

import java.util.UUID;

import org.apache.tomcat.util.http.parser.MediaType;

import com._labor.fakecord.domain.enums.ImageType;

public interface MediaService {
  String getAvatarUploadUrl(UUID userId, ImageType type);
  String getBannerUploadUrl(UUID userId, ImageType type);
}
