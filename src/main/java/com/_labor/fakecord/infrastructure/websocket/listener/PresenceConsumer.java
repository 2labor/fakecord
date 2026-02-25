package com._labor.fakecord.infrastructure.websocket.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com._labor.fakecord.domain.events.MusicUpdateEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class PresenceConsumer {
  
  private final SimpMessagingTemplate messagingTemplate;

  @KafkaListener(topics = "music-status-updates", groupId = "fakecord-ws-delivery")
  public void onMusicUpdate(MusicUpdateEvent event) {
    log.debug("PresenceConsumer: Received update for user {}", event.userId());

    String destination = "/topic/music." + event.userId();

    messagingTemplate.convertAndSend(destination, event);
  } 
}
