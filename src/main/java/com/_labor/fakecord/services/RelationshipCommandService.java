package com._labor.fakecord.services;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com._labor.fakecord.domain.dto.UserProfileShort;
import com._labor.fakecord.domain.enums.RelationshipStatus;

public interface RelationshipCommandService {
  void createFriendship(UUID userA, UUID userB);
  void removeFriend(UUID userId, UUID friendId);
  void blockUser(UUID senderId, UUID targetId);
  void unblockUser(UUID senderId, UUID targetId);
}
