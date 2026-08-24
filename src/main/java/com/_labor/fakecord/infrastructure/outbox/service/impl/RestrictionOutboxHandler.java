package com._labor.fakecord.infrastructure.outbox.service.impl;

import java.util.Set;

import org.springframework.stereotype.Component;

import com._labor.fakecord.domain.enums.ServerRestrictionType;
import com._labor.fakecord.infrastructure.cache.services.PermissionCache;
import com._labor.fakecord.infrastructure.outbox.domain.OutboxEvent;
import com._labor.fakecord.infrastructure.outbox.domain.OutboxEventType;
import com._labor.fakecord.infrastructure.outbox.domain.payload.ServerRestrictionAppliedPayload;
import com._labor.fakecord.infrastructure.outbox.domain.payload.ServerRestrictionDeactivatedPayload;
import com._labor.fakecord.infrastructure.outbox.service.OutboxHandler;
import com._labor.fakecord.services.ServerMemberService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class RestrictionOutboxHandler implements OutboxHandler {

  private final ObjectMapper objectMapper;
  private final ServerMemberService memberService;
  private final PermissionCache permissionCache;

  private final static Set<OutboxEventType> SUPPORTED = Set.of(
    OutboxEventType.SERVER_RESTRICTION_APPLIED,
    OutboxEventType.SERVER_RESTRICTION_DEACTIVATED
  ); 

  @Override
  public boolean supports(OutboxEventType type) {
    return SUPPORTED.contains(type);
  }

  @Override
  public void handle(OutboxEvent event) {
    try {
      switch (event.getType()) {
        case SERVER_RESTRICTION_APPLIED -> {
          ServerRestrictionAppliedPayload payload = objectMapper.readValue(event.getPayload(), ServerRestrictionAppliedPayload.class);
          if (payload.type() == ServerRestrictionType.BAN || payload.type() == ServerRestrictionType.KICK) {
            if (memberService.checkIsUserMember(payload.serverId(), payload.targetId())) {
              memberService.removeMemberFromServer(payload.targetId(), payload.serverId());
            }
          }
          permissionCache.evictUserServerPermission(payload.serverId(), payload.targetId());

          log.info("Applied side-effects for restriction [{}] on server {} for user {}", payload.type(), payload.serverId(), payload.targetId());
        }

        case SERVER_RESTRICTION_DEACTIVATED -> {
          ServerRestrictionDeactivatedPayload payload = objectMapper.readValue(
            event.getPayload(), ServerRestrictionDeactivatedPayload.class
          );

          permissionCache.evictUserServerPermission(payload.serverId(), payload.targetId());

          log.info("Deactivated side-effects for restriction [{}] on server {} for user {}", payload.type(), payload.serverId(), payload.targetId());
        }
      }
    } catch (Exception e) {
      log.error("Failed to process restriction outbox event {}: {}", event.getId(), e.getMessage(), e);
      throw new RuntimeException("Restriction outbox processing failed", e);
    }
  }
  
}
