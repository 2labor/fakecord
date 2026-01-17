package com._labor.fakecord.controller;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com._labor.fakecord.domain.dto.ChatMessageDto;
import com._labor.fakecord.domain.entity.ChatMessage;
import com._labor.fakecord.domain.mappper.ChatMessageMapper;
import com._labor.fakecord.services.ChatMessageServices;

@Controller
public class ChatController {

  private final ChatMessageMapper chatMapper;
  private final ChatMessageServices service;

  public ChatController(ChatMessageMapper chatMapper, ChatMessageServices service) {
    this.chatMapper = chatMapper;
    this.service = service;
  }

  @MessageMapping("/chat.send") // parse message from front-end that was sended on "/app/chat.send"
  @SendTo("/topic/public") // push message to all subscribers of canal "/topic/public"
  public ChatMessageDto sendMessage(@Payload ChatMessageDto dto) {
    ChatMessage message = chatMapper.fromDto(dto);

    ChatMessage savedMessage = service.createMessage(message);

    return chatMapper.toDto(savedMessage);
  }
}
