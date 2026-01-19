package com._labor.fakecord.interceptor;

import jakarta.validation.Validator;

import jakarta.validation.ConstraintViolationException;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
public class ValidationInterceptor implements ChannelInterceptor {
  private final Validator validator;

  public ValidationInterceptor(Validator validator) {
    this.validator = validator;
  }

  @Override
  public  Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

    if (null != accessor && "SEND".equals(accessor.getCommand().name())) {
      Object payload = message.getPayload();
      var violations = validator.validate(payload);

      if (!violations.isEmpty()) {
        throw new ConstraintViolationException(violations);
      }
    }
    return message;
  }
}
