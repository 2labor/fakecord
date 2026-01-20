package com._labor.fakecord.services.impl;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com._labor.fakecord.domain.entity.ChatMessage;
import com._labor.fakecord.domain.entity.MessageType;
import com._labor.fakecord.domain.entity.User;
import com._labor.fakecord.repository.ChatMessageRepository;
import com._labor.fakecord.repository.UserRepository;
import com._labor.fakecord.services.ChatMessageServices;

import jakarta.transaction.Transactional;

@Service
public class ChatMessageImpl implements ChatMessageServices {

  private final ChatMessageRepository repository;
  private final UserRepository userRepository;

  public ChatMessageImpl(ChatMessageRepository repository, UserRepository userRepository) {
    this.repository = repository;
    this.userRepository = userRepository;
  } 

  @Override
  @Transactional
  public ChatMessage createMessage(ChatMessage message) {
    if (null == message.getContent() || message.getContent().trim().isEmpty()) {
      throw new IllegalArgumentException("Message cannot be empty!");
    }

    User author = userRepository.findByName(message.getUser().getName())
      .orElseThrow(() -> new IllegalArgumentException("Author not found!"));

    message.setUser(author);
    if (message.getType() == null) {
      message.setType(MessageType.SEND);
    }

    return repository.save(message);
  }

  @Override
  @Transactional
  public ChatMessage updChatMessage(UUID chatMessageId, ChatMessage message) {
    ChatMessage existing = repository.findById(chatMessageId)
      .orElseThrow(() -> new IllegalArgumentException("Message not found!"));

      if (message.getContent() != null && !message.getContent().trim().isEmpty()) {
        existing.setContent(message.getContent());
      } else {
        throw new IllegalArgumentException("New content cannot be empty");
      }

      return repository.save(existing);
  }

  @Override
  public Page<ChatMessage> getMessagesPage(Pageable pageable) {
    return repository.findAll(pageable);
  }
  
}
