package com._labor.fakecord.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com._labor.fakecord.model.ChatMessage;
import com._labor.fakecord.model.MessageType;

@Component
public class WebSocketEventListener {
  private final Logger log = LoggerFactory.getLogger(WebSocketEventListener.class);

  private final SimpMessageSendingOperations messagingTemplate;

  public WebSocketEventListener(SimpMessageSendingOperations messagingTemplate) {
    this.messagingTemplate = messagingTemplate;
  }

  @EventListener
  public void handleWebsocketDisconnectListener(SessionDisconnectEvent event) {
    StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

    String username = (String) headerAccessor.getSessionAttributes().get("username");

    if (username != null) {
      log.info("User disconnected: " + username);

      ChatMessage chatMessage = new ChatMessage();
      chatMessage.setType(MessageType.LEAVE);
      chatMessage.setUserName(username);

      messagingTemplate.convertAndSend("/topic/public", chatMessage);
    }
  }
}
