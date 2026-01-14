package com._labor.fakecord.domain.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com._labor.fakecord.domain.entity.MessageType;

public record ChatMessageDto (
  UUID id,
  String content,
  MessageType type,
  User user,
  LocalDateTime timestamp
) {}
