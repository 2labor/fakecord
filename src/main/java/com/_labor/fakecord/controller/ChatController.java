package com._labor.fakecord.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com._labor.fakecord.domain.entity.ChatMessage;

@Controller
public class ChatController {
  @MessageMapping("/general")
  @SendTo("/topic/general")
  public ChatMessage sendMessage(ChatMessage ms) {
    return
  }
}
