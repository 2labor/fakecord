package com._labor.fakecord.services;

import java.util.List;
import java.util.UUID;

import com._labor.fakecord.domain.entity.ChatMessage;

public interface ChatMessageServices {
  ChatMessage createMessage(ChatMessage message);
  ChatMessage updChatMessage(UUID chatMessageId,ChatMessage message);
  List<ChatMessage> getLastMessages(int count);
}
 