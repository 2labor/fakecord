package com._labor.fakecord.interceptor;

import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.UUID;

import org.springframework.messaging.Message;

import com._labor.fakecord.security.versions.TokenVersionManager;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class WebSocketTokenFilter implements ChannelInterceptor {
    private final TokenVersionManager tokenVersionManager;

    public WebSocketTokenFilter(TokenVersionManager tokenVersionManager) {
        this.tokenVersionManager = tokenVersionManager;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (null == accessor || accessor.getCommand() == null) return message;

        switch (accessor.getCommand()) {
            case CONNECT -> handleConnection(accessor);
            case SUBSCRIBE, SEND -> handleSend(accessor);
            default -> {}
        }
        return message;
    }

    private void handleConnection(StompHeaderAccessor accessor) {
        Principal user = accessor.getUser();

        if (user == null) {
            log.error("WS Connection denied: No user principal found");
            throw new MessageDeliveryException("MISSING_TOKEN");
        }

        String userId = user.getName(); 
        int currentVersion = tokenVersionManager.getCurrentVersion(UUID.fromString(userId));

        if (accessor.getSessionAttributes() != null) {
            accessor.getSessionAttributes().put("userId", userId);
            accessor.getSessionAttributes().put("tokenVersion", currentVersion);
        }
        
        log.debug("User {} connected to WS with version {}", userId, currentVersion);
    }

    private void handleSend(StompHeaderAccessor accessor) {
        if (accessor.getSessionAttributes() == null) return;

        String userId = (String) accessor.getSessionAttributes().get("userId");
        if (userId == null) return;

        int currentVersionInRedis = tokenVersionManager.getCurrentVersion(UUID.fromString(userId));
        Integer sessionVersion = (Integer) accessor.getSessionAttributes().get("tokenVersion");

        if (sessionVersion != null && sessionVersion < currentVersionInRedis) {
            log.warn("Blocking WS action for user {}: outdated version", userId);
            throw new MessageDeliveryException("TOKEN_OUTDATED");
        }
    }
}