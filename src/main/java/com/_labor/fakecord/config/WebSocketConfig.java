package com._labor.fakecord.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
  @Override 
  public void configureMessageBroker(MessageBrokerRegistry registry) {

    /**
     * manages goes from server to client starts from "/topic"
     * client will subscribed on canal "/topic/public" 
     */
    registry.enableSimpleBroker("/topic");

    /**
     * manages goes from client to server starts from "/app"
     */
    registry.setApplicationDestinationPrefixes("/app");
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    // handshake
    registry.addEndpoint("/ws-chat")
      .setAllowedOriginPatterns("*")
      .withSockJS();
  }
}