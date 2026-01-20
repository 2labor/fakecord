package com._labor.fakecord.domain.mappper.Impl;

import org.springframework.stereotype.Component;

import com._labor.fakecord.domain.dto.ChatMessageDto;
import com._labor.fakecord.domain.entity.ChatMessage;
import com._labor.fakecord.domain.mappper.ChatMessageMapper;
import com._labor.fakecord.domain.mappper.UserMapper;

@Component
public class ChatMessageMapperImpl implements ChatMessageMapper {

  private final UserMapper userMapper;

  public ChatMessageMapperImpl(UserMapper userMapper) {
    this.userMapper = userMapper;
  }

  @Override
  public ChatMessage fromDto(ChatMessageDto dto) {
    if (null == dto) return null;

    return new ChatMessage(
      dto.id(),
      dto.content(), 
      dto.type(),
      userMapper.fromDto(dto.userDto())
    );
  }

  @Override
  public ChatMessageDto toDto(ChatMessage message) {

    if (null == message) return null;

    return new ChatMessageDto(
      message.getId(),
      message.getContent(),
      message.getType(),
      userMapper.toDto(message.getUser()),
      message.getCreatedAt()
    );
  }
  
}
