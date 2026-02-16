package com._labor.fakecord.domain.mappper;

import com._labor.fakecord.domain.dto.ChatMessageDto;
import com._labor.fakecord.domain.entity.ChatMessage;
import com._labor.fakecord.domain.entity.UserProfile;

public interface ChatMessageMapper {
  ChatMessageDto toDto(ChatMessage message, UserProfile profile);
  ChatMessage fromDto(ChatMessageDto dto);
}
