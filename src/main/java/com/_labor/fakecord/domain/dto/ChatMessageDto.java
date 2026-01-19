package com._labor.fakecord.domain.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com._labor.fakecord.domain.entity.MessageType;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChatMessageDto (
  UUID id,
  @NotBlank(message = "Message cannot be empty")
  @Size(max = 364, message = "Length of a massage could be maximum of 364 characters long")
  String content,
  MessageType type,
  @Valid
  UserDto userDto,
  LocalDateTime timestamp
) {}
