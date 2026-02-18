package com._labor.fakecord.interceptor;

import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

import java.util.UUID;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import com._labor.fakecord.services.UserStatusService;

@Component
public class PresenceInterceptor implements ChannelInterceptor {

  private final UserStatusService statusService;

  public PresenceInterceptor(UserStatusService statusService) {
    this.statusService = statusService;
  }

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

    if (null != accessor && null != accessor.getCommand()) {
      String userId = (String) (accessor.getSessionAttributes() != null ? accessor.getSessionAttributes().get("userId") : null);

      if (null != userId) {
        statusService.setOnline(UUID.fromString(userId));
      }
    }
    
    return message;
  }
}
