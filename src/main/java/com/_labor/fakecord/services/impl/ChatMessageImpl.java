package com._labor.fakecord.services.impl;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
  public ChatMessage createMessage(ChatMessage message) {
    if (null != message.getId() || message.getContent().trim().isEmpty()) {
      message.setType(MessageType.ERROR);
      throw new IllegalArgumentException("Message have to have context");
    }

    if (null == message.getUser()) {
      message.setType(MessageType.ERROR);
      throw new IllegalArgumentException("Message have to have author!");
    }

    User user = userRepository.findByName(message.getUser().getName())
      .orElseGet(() -> {
        LocalDateTime now = LocalDateTime.now();
        return userRepository.save(new User(null, message.getUser().getName(), now, now));
      }
    );

    LocalDateTime now = LocalDateTime.now();
    return repository.save(new ChatMessage(
      null,
      message.getContent(), 
      message.getType(), 
      user,
      now, 
      now));
  }

  @Transactional
  @Override
  public ChatMessage updChatMessage(UUID chatMessageId,ChatMessage message) {
    if (null == message.getId()) {
      throw new IllegalArgumentException("Message don't have an id!");
    }

    if (!Objects.equals(chatMessageId, message.getId())) {
      throw new IllegalArgumentException("Attempt to change message id that is not present!");
    }

    if (message.getContent().trim().isEmpty()) {
      throw new IllegalArgumentException("Message don't have any content");
    }

    ChatMessage existingChatMessage = repository.findById(chatMessageId).orElseThrow(() -> new IllegalArgumentException("Message not found!"));

    existingChatMessage.setContent(message.getContent());
    existingChatMessage.setUpdatedAt(LocalDateTime.now());

    return repository.save(existingChatMessage);
  }

  @Override
  public Page<ChatMessage> getMessagesPage(Pageable pageable) {
    return repository.findAll(pageable);
  }
  
}
