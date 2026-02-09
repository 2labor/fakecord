package com._labor.fakecord.services.impl;

import java.util.UUID;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com._labor.fakecord.config.RabbitMQConfig;
import com._labor.fakecord.domain.entity.EmailIdentity;
import com._labor.fakecord.repository.EmailIdentityRepository;
import com._labor.fakecord.repository.UserRepository;
import com._labor.fakecord.services.RegistrationExpiryConsumer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class RegistrationExpiryConsumerImpl implements RegistrationExpiryConsumer {

  private final UserRepository userRepository;
  private final EmailIdentityRepository emailIdentityRepository;
  private final ObjectMapper mapper;

  public RegistrationExpiryConsumerImpl(
    UserRepository userRepository,
    EmailIdentityRepository emailIdentityRepository,
    ObjectMapper mapper
  ) {
    this.userRepository = userRepository;
    this.emailIdentityRepository = emailIdentityRepository;
    this.mapper = mapper;
  }

  @Override
  @Transactional
  @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
  public void handleExpiry(String message) {
    try { 
      JsonNode root = mapper.readTree(message);
      UUID userId = UUID.fromString(root.get("userId").asText());

      emailIdentityRepository.findByUserId(userId).stream()
        .filter(EmailIdentity::isPrimary)
        .findFirst()
        .ifPresentOrElse(
          identity -> {
            if (identity.isVerified()) {
              log.info("User {} confirmed email. Deletion aborted.", userId);
            } else {
              executeHardDelete(userId);
            }
          },
          () -> log.warn("No identity found for user {}. Likely already deleted.", userId)
        );
    } catch (Exception e) {
      log.error("Critical error in ExpiryConsumer. Message will be re-queued.", e);
      throw new RuntimeException(e);
    }
  }
  
 private void executeHardDelete(UUID userId) {
    log.warn("Hard Delete Triggered for: {}", userId);
    userRepository.findById(userId).ifPresent(user -> {
        userRepository.delete(user);
        userRepository.flush(); 
        log.info("SQL DELETE command sent to database for user {}", userId);
    });
}
}
