package com._labor.fakecord.domain.events;

import java.util.UUID;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

@Getter
public class PasswordResetRequestedEvent extends ApplicationEvent {

  private final String email;
  private final String token;
  private final UUID userId; 
  
  public PasswordResetRequestedEvent(Object source, String email, String token, UUID userId) {
    super(source);
    this.email = email;
    this.token = token;
    this.userId = userId;
  }
  
}
