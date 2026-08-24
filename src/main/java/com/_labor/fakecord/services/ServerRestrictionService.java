package com._labor.fakecord.services;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com._labor.fakecord.domain.entity.ServerRestriction;
import com._labor.fakecord.domain.enums.ServerRestrictionType;

public interface ServerRestrictionService {
  ServerRestriction setRestriction(UUID operatorId, ServerRestriction request);
  void deactivateRestriction(UUID operatorId, Long restrictionId);
  
  ServerRestriction getRestriction(UUID operatorId, Long restrictionId);

  List<ServerRestriction> getActiveUserRestrictions(UUID operatorId, UUID targetId, Long serverId);
  
  Slice<ServerRestriction> getAllUserRestrictions(UUID operatorId, UUID targetId, Long serverId, Pageable pageable);
  Slice<ServerRestriction> getActiveRestrictionsByType(UUID operatorId, Long serverId, ServerRestrictionType type, Pageable pageable);
  
  Slice<ServerRestriction> getServerRestrictionsHistory(UUID operatorId, Long serverId, Pageable pageable);
}