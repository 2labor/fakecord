package com._labor.fakecord.domain.mappper.Impl;

import org.springframework.stereotype.Component;

import com._labor.fakecord.domain.dto.ChatMessageDto;
import com._labor.fakecord.domain.dto.UserProfileShort;
import com._labor.fakecord.domain.dto.UserStatus;
import com._labor.fakecord.domain.entity.ChatMessage;
import com._labor.fakecord.domain.entity.UserProfile;
import com._labor.fakecord.domain.mappper.ChatMessageMapper;
import com._labor.fakecord.domain.mappper.UserMapper;
import com._labor.fakecord.domain.mappper.UserProfileMapper;

@Component
public class ChatMessageMapperImpl implements ChatMessageMapper {

  private final UserProfileMapper profileMapper;

  public ChatMessageMapperImpl(UserProfileMapper profileMapper) {
    this.profileMapper = profileMapper;
  }

  @Override
  public ChatMessage fromDto(ChatMessageDto dto) {
    if (null == dto) return null;

    ChatMessage message = new ChatMessage();
    message.setContent(dto.content());
    message.setType(dto.type());

    return message;
  }

  @Override
  public ChatMessageDto toDto(ChatMessage message, UserProfile profile) {

    if (null == message) return null;

  
    return new ChatMessageDto(
      message.getId(),
      message.getContent(),
      message.getType(),
      profileMapper.toShortDto(profile, UserStatus.OFFLINE),
      message.getCreatedAt()
    );
  }
  
}
