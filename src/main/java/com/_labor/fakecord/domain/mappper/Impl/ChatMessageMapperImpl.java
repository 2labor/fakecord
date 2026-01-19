package com._labor.fakecord.domain.mappper.Impl;

import org.springframework.stereotype.Component;

import com._labor.fakecord.domain.dto.ChatMessageDto;
import com._labor.fakecord.domain.dto.UserDto;
import com._labor.fakecord.domain.entity.ChatMessage;
import com._labor.fakecord.domain.entity.User;
import com._labor.fakecord.domain.mappper.ChatMessageMapper;

@Component
public class ChatMessageMapperImpl implements ChatMessageMapper {

  @Override
  public ChatMessage fromDto(ChatMessageDto dto) {

    User user = null;
    if (null != dto.userDto()) {
      user = new User(
        dto.userDto().id(),
        dto.userDto().name()
      );
    }

    return new ChatMessage(
      dto.id(),
      dto.content(), 
      dto.type(),
      user,
      null, 
      null
    );
  }

  @Override
  public ChatMessageDto toDto(ChatMessage message) {

    UserDto userDto = null;
    if (null != message.getUser()) {
        userDto = new UserDto(
        message.getUser().getId(), 
        message.getUser().getName()
      );
    }

    return new ChatMessageDto(
      message.getId(),
      message.getContent(),
      message.getType(),
      userDto,
      message.getCreatedAt()
    );
  }
  
}
