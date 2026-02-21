package com._labor.fakecord.infrastructure.storage;

public interface FileStorageService {
  String generateUploadUrl(String objectPath, String contentType, long maxFileSize);
  void delete(String objectPath);
}
