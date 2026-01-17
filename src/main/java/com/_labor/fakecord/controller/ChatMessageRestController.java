package com._labor.fakecord.controller;

import org.springframework.web.bind.annotation.RestController;

import com._labor.fakecord.domain.dto.ChatMessageDto;
import com._labor.fakecord.domain.mappper.ChatMessageMapper;
import com._labor.fakecord.services.ChatMessageServices;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/messages")
public class ChatMessageRestController {
  private final ChatMessageMapper mapper;
  private final ChatMessageServices services;

  public ChatMessageRestController(
    ChatMessageMapper mapper,
    ChatMessageServices services
  ) {
    this.mapper = mapper;
    this.services = services;
  }

  @GetMapping
  public List<ChatMessageDto> getMessages(@RequestParam(defaultValue = "20") int limit) {
    return services.getLastMessages(limit)
      .stream()
      .map(mapper::toDto)
      .collect(Collectors.toList());
  }
}
