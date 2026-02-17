package com._labor.fakecord.services;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com._labor.fakecord.domain.dto.ChatMessageDto;
import com._labor.fakecord.domain.entity.ChatMessage;

public interface ChatMessageServices {
  ChatMessageDto createMessage(ChatMessage message, String userIdStr);
  ChatMessage updChatMessage(UUID chatMessageId,ChatMessage message);
  List<ChatMessageDto> getMessagesPage(Pageable pageable);
}
 