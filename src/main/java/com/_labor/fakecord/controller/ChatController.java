package com._labor.fakecord.controller;

import java.util.Optional;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import com._labor.fakecord.model.ChatMessage;
import com._labor.fakecord.model.MessageType;
import com._labor.fakecord.services.ChatValidator;

@Controller
public class ChatController {

  private final ChatValidator chatValidator;

  public ChatController(ChatValidator chatValidator) {
    this.chatValidator = chatValidator;
  }

  @MessageMapping("/chat.sendMessage")
  @SendTo("/topic/public")
  public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
    return chatValidator.validate(chatMessage)
      .orElseGet(() ->{
        System.out.println("LOG: Invalid message dropped");
        return null;
      });
  }

  @MessageMapping("/chat.addUser")
  @SendTo("/topic/public")
  public ChatMessage addUser(
    @Payload ChatMessage chatMessage,
    SimpMessageHeaderAccessor headerAccessor 
  ) {
    return chatValidator.validate(chatMessage).map(validMsg -> {
      headerAccessor.getSessionAttributes().put("username", validMsg.getUserName());
      return validMsg;
    }).orElseGet(() -> {
      chatMessage.setType(MessageType.ERROR);
      chatMessage.setContent("User name has to be from 2 characters to 20!");
      return chatMessage;
    });
  }
}
