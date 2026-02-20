package com._labor.fakecord.infrastructure.websocket.listener;

import java.util.UUID;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com._labor.fakecord.services.UserStatusService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class WebSocketEventListener {
  
  private final UserStatusService statusService;

  public WebSocketEventListener(UserStatusService statusService) {
    this.statusService = statusService;
  }

  @EventListener
  public void handleWebSocketConnectListener(SessionConnectEvent event) {
    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
    String userId = getUserId(accessor);

    if (null != userId) {
      statusService.setOnline(UUID.fromString(userId));
      log.debug("Presence: User {} connected", userId);
    }
  }

  @EventListener
  public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
    String userId = getUserId(accessor);

    if (userId != null) {
      statusService.setOffline(UUID.fromString(userId));
      log.debug("Presence: User {} disconnected", userId);
    }
  }

  private String getUserId(StompHeaderAccessor accessor) {
    if (null == accessor.getSessionAttributes()) return null;
    return (String) accessor.getSessionAttributes().get("userId");
  }

}
