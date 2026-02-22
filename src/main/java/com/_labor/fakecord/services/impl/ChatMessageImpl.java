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
import com._labor.fakecord.domain.dto.UserProfileFullDto;
import com._labor.fakecord.domain.dto.UserProfileShort;
import com._labor.fakecord.domain.entity.ChatMessage;
import com._labor.fakecord.domain.entity.MessageType;
import com._labor.fakecord.domain.entity.User;
import com._labor.fakecord.domain.entity.UserProfile;
import com._labor.fakecord.domain.enums.UserStatus;
import com._labor.fakecord.domain.mappper.ChatMessageMapper;
import com._labor.fakecord.domain.mappper.UserProfileMapper;
import com._labor.fakecord.repository.ChatMessageRepository;
import com._labor.fakecord.repository.UserProfileRepository;
import com._labor.fakecord.repository.UserRepository;
import com._labor.fakecord.services.ChatMessageServices;
import com._labor.fakecord.services.UserProfileCache;

import jakarta.transaction.Transactional;

@Service
public class ChatMessageImpl implements ChatMessageServices {

  private final ChatMessageRepository repository;
  private final UserRepository userRepository;
  private final UserProfileCache cache;
  private final UserProfileMapper profileMapper;
  private final ChatMessageMapper mapper;

  public ChatMessageImpl(
    ChatMessageRepository repository, 
    UserRepository userRepository, 
    UserProfileCache cache, 
    UserProfileMapper profileMapper,
    ChatMessageMapper mapper
  ) {
    this.repository = repository;
    this.userRepository = userRepository;
    this.cache = cache;
    this.profileMapper = profileMapper;
    this.mapper = mapper;
  } 

  @Override
  @Transactional
  public ChatMessageDto createMessage(ChatMessage message, String userIdStr) {
    UUID userId = UUID.fromString(userIdStr); 

    if (null == message.getContent() || message.getContent().trim().isEmpty()) {
      throw new IllegalArgumentException("Message cannot be empty!");
    }

    User authorProxy = userRepository.getReferenceById(userId);
    message.setUser(authorProxy);

    if (message.getType() == null) {
      message.setType(MessageType.SEND);
    }
    
    ChatMessage savedMessage = repository.save(message);

    UserProfileFullDto profileFull = cache.getUserProfile(userId);

    UserProfileShort shortProfile = profileMapper.toShortDto(profileFull, UserStatus.OFFLINE);

    return mapper.toDto(savedMessage, shortProfile); 
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

    Map<UUID, UserProfileShort> profiles = userIds.stream()
      .collect(Collectors.toMap(
        id -> id,
        id -> {
          UserProfileFullDto full = cache.getUserProfile(id);
          return profileMapper.toShortDto(full, UserStatus.OFFLINE);
        },
        (existing, replacement) -> existing
      ));

    return massages.stream()
      .map(m -> mapper.toDto(m, profiles.get(m.getUser().getId())))
      .collect(Collectors.toList());
  }
  
}
