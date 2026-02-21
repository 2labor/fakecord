package com._labor.fakecord.infrastructure.storage;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Slf4j
@Service
public class S3FileStorageServiceImpl implements FileStorageService {

  private final S3Presigner s3Presigner;
  private final S3Client s3Client;

  @Value("${app.s3.bucket-name}")
  private String bucketName;

  public S3FileStorageServiceImpl(S3Presigner s3Presigner, S3Client s3Client) {
    this.s3Presigner = s3Presigner;
    this.s3Client = s3Client;
  }

  @Override
  public String generateUploadUrl(String objectPath, String contentType) {
    log.debug("Generating presigned URL for path: {} [type: {}]", objectPath, contentType);

    PutObjectRequest objectRequest = PutObjectRequest.builder()
      .bucket(bucketName)
      .key(objectPath)
      .contentType(contentType)
      .build();
    
    PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
      .signatureDuration(Duration.ofMinutes(15))
      .putObjectRequest(objectRequest)
      .build();

    return s3Presigner.presignPutObject(presignRequest).url().toString();
  }

  @Override
  public void delete(String objectPath) {
    try {
      log.info("Deleting object from S3: {}/{}", bucketName, objectPath);

      DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
        .bucket(bucketName)
        .key(objectPath)
        .build();

      s3Client.deleteObject(deleteObjectRequest);

      log.debug("Object {} successfully deleted", objectPath);
    } catch (Exception e) {
      log.error("Failed to delete object {} from S3", objectPath, e);
    }
  }
  
}
