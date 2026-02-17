package com._labor.fakecord.services.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com._labor.fakecord.domain.dto.ChatMessageDto;
import com._labor.fakecord.domain.entity.ChatMessage;
import com._labor.fakecord.domain.entity.MessageType;
import com._labor.fakecord.domain.entity.User;
import com._labor.fakecord.domain.entity.UserProfile;
import com._labor.fakecord.domain.mappper.ChatMessageMapper;
import com._labor.fakecord.repository.ChatMessageRepository;
import com._labor.fakecord.repository.UserProfileRepository;
import com._labor.fakecord.repository.UserRepository;
import com._labor.fakecord.services.ChatMessageServices;

import jakarta.transaction.Transactional;

@Service
public class ChatMessageImpl implements ChatMessageServices {

  private final ChatMessageRepository repository;
  private final UserRepository userRepository;
  private final UserProfileRepository userProfileRepository;
  private final ChatMessageMapper mapper;

  public ChatMessageImpl(ChatMessageRepository repository, UserRepository userRepository, UserProfileRepository userProfileRepository, ChatMessageMapper mapper) {
    this.repository = repository;
    this.userRepository = userRepository;
    this.userProfileRepository = userProfileRepository;
    this.mapper = mapper;
  } 

  @Override
  @Transactional
  public ChatMessageDto createMessage(ChatMessage message, String userIdStr) {
    UUID userId = UUID.fromString(userIdStr); 

    if (null == message.getContent() || message.getContent().trim().isEmpty()) {
      throw new IllegalArgumentException("Message cannot be empty!");
    }

    User author = userRepository.findById(userId)
      .orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " not found!"));

    message.setUser(author);
    if (message.getType() == null) {
      message.setType(MessageType.SEND);
    }
    
    ChatMessage savedMessage = repository.save(message);

    UserProfile profile = userProfileRepository.findById(userId)
      .orElse(null);

    return mapper.toDto(savedMessage, profile); 
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
  public List<ChatMessageDto> getMessagesPage(Pageable pageable) {
    Page<ChatMessage> massages = repository.findAll(pageable);

    Set<UUID> userIds = massages.stream()
      .map(m -> m.getUser().getId())
      .collect(Collectors.toSet());

    Map<UUID, UserProfile> profiles = userProfileRepository.findAllByUserIdIn(userIds)
      .stream()
      .collect(Collectors.toMap(p -> p.getUser().getId(), p -> p));

    return massages.stream()
      .map(m -> mapper.toDto(m, profiles.get(m.getUser().getId())))
      .collect(Collectors.toList());
  }
  
}
