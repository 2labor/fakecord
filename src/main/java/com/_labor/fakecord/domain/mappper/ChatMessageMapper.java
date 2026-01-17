package com._labor.fakecord.domain.mappper;

import com._labor.fakecord.domain.dto.ChatMessageDto;
import com._labor.fakecord.domain.entity.ChatMessage;

public interface ChatMessageMapper {
  ChatMessage fromDto(ChatMessageDto dto);
  ChatMessageDto toDto(ChatMessage message);
}
