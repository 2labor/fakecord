package com._labor.fakecord.services;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com._labor.fakecord.domain.entity.ChatMessage;

public interface ChatMessageServices {
  ChatMessage createMessage(ChatMessage message);
  ChatMessage updChatMessage(UUID chatMessageId,ChatMessage message);
  Page<ChatMessage> getMessagesPage(Pageable pageable);
}
 