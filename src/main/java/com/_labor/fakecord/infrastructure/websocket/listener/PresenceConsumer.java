package com._labor.fakecord.infrastructure.websocket.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com._labor.fakecord.domain.events.MusicUpdateEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class PresenceConsumer {
  
  private final SimpMessagingTemplate messagingTemplate;
  private final ObjectMapper objectMapper;

  @KafkaListener(topics = "music-status-updates", groupId = "fakecord-ws-delivery")
  public void onMusicUpdate(@Payload String message) {
    try {
      MusicUpdateEvent event = objectMapper.readValue(message, MusicUpdateEvent.class);

      messagingTemplate.convertAndSend(
        "/topic/music." + event.userId(),
        event
      );
      log.debug("Received music update for user {}: {}", event.userId(), event.trackName());
        
    } catch (Exception e) {
        log.error("Failed to process music update: {}", message, e);
    }
  } 
}
