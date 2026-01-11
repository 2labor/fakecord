package com._labor.fakecord.services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com._labor.fakecord.model.ChatMessage;
import com._labor.fakecord.model.MessageType;

@Service
public class ChatValidator {
  public Optional<ChatMessage> validate(ChatMessage message) {
    return Optional.ofNullable(message)
    .filter(this::isUserNameValid)
    .filter(this::isContentValid);
  }

  private boolean isUserNameValid(ChatMessage m) {
    String name = m.getUserName();
    return name != null && !name.trim().isEmpty() && name.length() >= 2 && name.length() <= 20;
  }

  private boolean isContentValid(ChatMessage m) {
    if (m.getType() != MessageType.CHAT) return true;

    String content = m.getContent();
    return content != null && !content.trim().isEmpty() && content.length() <= 500;
  }
}
