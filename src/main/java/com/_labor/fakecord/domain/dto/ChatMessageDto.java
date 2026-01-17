package com._labor.fakecord.domain.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com._labor.fakecord.domain.entity.MessageType;
import com._labor.fakecord.domain.entity.User;

public record ChatMessageDto (
  UUID id,
  String content,
  MessageType type,
  UserDto userDto,
  LocalDateTime timestamp
) {}
