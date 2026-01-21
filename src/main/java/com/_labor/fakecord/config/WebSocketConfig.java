package com._labor.fakecord.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.DefaultManagedTaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com._labor.fakecord.interceptor.ValidationInterceptor;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  @Autowired
  private ValidationInterceptor validator;

  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    registration.interceptors(validator);
  }

  @Override 
  public void configureMessageBroker(MessageBrokerRegistry registry) {

    /**
     * manages goes from server to client starts from "/topic"
     * client will subscribed on canal "/topic/public" 
     */
    registry.enableSimpleBroker("/topic")
    .setHeartbeatValue(new long[]{10000, 10000})
    .setTaskScheduler(heartbeatScheduler());
    
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

  @Bean
  public TaskScheduler heartbeatScheduler() {
    return new ThreadPoolTaskScheduler();
  }
}