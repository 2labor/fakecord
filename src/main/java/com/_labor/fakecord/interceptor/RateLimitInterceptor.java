package com._labor.fakecord.interceptor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import com._labor.fakecord.security.ratelimit.service.RateLimiterService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RateLimitInterceptor implements ChannelInterceptor{
  private final RateLimiterService service;
  private final SimpMessagingTemplate messagingTemplate;
  private final int capacity;
  private final int refillSeconds;

  public RateLimitInterceptor(
        RateLimiterService service,
        @Lazy SimpMessagingTemplate messagingTemplate,
        @Value("${app.ratelimit.chat.capacity:7}") int capacity,
        @Value("${app.ratelimit.chat.refill-seconds:5}") int refillSeconds
      ) {
      this.service = service;
      this.messagingTemplate = messagingTemplate;
      this.capacity = capacity;
      this.refillSeconds = refillSeconds;
    }

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

    if (null != accessor && StompCommand.SEND.equals(accessor.getCommand())) {
      String sessionId = accessor.getSessionId();
      String userIdentifier = (null != accessor.getUser()) 
        ? accessor.getUser().getName() 
        : sessionId;
      
      String key = "ws_chat_limit:" + userIdentifier;

      boolean allowed = service.tryConsume(key, capacity, refillSeconds);

      if (!allowed) {
        log.warn("STOMP Rate limit exceeded for user: {}. Limits: {}/{}s", 
        userIdentifier, capacity, refillSeconds);
        
        messagingTemplate.convertAndSendToUser(
          sessionId,
          "/queue/errors",
          "You send messages to fast! Please try again later",
          createHeaders(sessionId)
        );
        return null;
      }
    }
    return message;
  }

  private MessageHeaders createHeaders(String sessionId) {
    SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);    
    headerAccessor.setSessionId(sessionId);
    headerAccessor.setLeaveMutable(true);
    return headerAccessor.getMessageHeaders();
  }
}