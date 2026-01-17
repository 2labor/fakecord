package com._labor.fakecord.services;

import java.util.UUID;

import com._labor.fakecord.domain.entity.ChatMessage;

public interface ChatMessageServices {
  ChatMessage createMessage(ChatMessage message);
  ChatMessage updChatMessage(UUID chatMessageId,ChatMessage message);
}
 