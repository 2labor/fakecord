package com._labor.fakecord.controller;

import java.security.Principal;

import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import com._labor.fakecord.domain.dto.ChatMessageDto;
import com._labor.fakecord.domain.entity.ChatMessage;
import com._labor.fakecord.domain.entity.MessageType;
import com._labor.fakecord.domain.mappper.ChatMessageMapper;
import com._labor.fakecord.services.ChatMessageServices;

import jakarta.validation.Valid;

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
  public ChatMessageDto sendMessage(@Payload @Valid ChatMessageDto dto, Principal principal) {
    ChatMessage message = chatMapper.fromDto(dto);

    return service.createMessage(message, principal.getName());
  }

  @MessageExceptionHandler(MethodArgumentNotValidException.class)
  @SendToUser("/topic/errors")
  public String handleValidationException(MethodArgumentNotValidException ex) {
    return ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
  }
}
