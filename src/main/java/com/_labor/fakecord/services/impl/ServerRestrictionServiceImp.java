package com._labor.fakecord.services.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com._labor.fakecord.domain.entity.ServerRestriction;
import com._labor.fakecord.domain.enums.ServerRestrictionType;
import com._labor.fakecord.infrastructure.id.IdGenerator;
import com._labor.fakecord.infrastructure.outbox.domain.OutboxEventType;
import com._labor.fakecord.infrastructure.outbox.domain.payload.ServerRestrictionAppliedPayload;
import com._labor.fakecord.infrastructure.outbox.domain.payload.ServerRestrictionDeactivatedPayload;
import com._labor.fakecord.infrastructure.outbox.service.OutboxService;
import com._labor.fakecord.repository.ServerRestrictionRepository;
import com._labor.fakecord.services.PermissionService;
import com._labor.fakecord.services.ServerMemberService;
import com._labor.fakecord.services.ServerRestrictionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServerRestrictionServiceImp implements ServerRestrictionService {

  private final ServerRestrictionRepository repository;
  private final ServerMemberService memberService;
  private final IdGenerator idGenerator;
  private final OutboxService outboxService;
  private final PermissionService permissionService;

  @Override
  @Transactional
  public ServerRestriction setRestriction(UUID operatorId, ServerRestriction request) {
    requestAccess(operatorId, request.getServerId());

    if (!request.getType().equals(ServerRestrictionType.BAN) && !memberService.checkIsUserMember(request.getServerId(), request.getTargetId())) {
      throw new IllegalArgumentException("You cannot you " + request.getType().getDisplayName() + "for user that isn't server member!");
    }

    if (checkForActiveRestriction(request.getTargetId(), request.getServerId(), request.getType())) {
      throw new IllegalArgumentException("User already has restriction with such type: " + request.getType());
    }

    Long restrictionId = idGenerator.nextId();
    
    ServerRestriction restriction = ServerRestriction.builder()
      .id(restrictionId)
      .serverId(request.getServerId())
      .targetId(request.getTargetId())
      .operatorId(request.getOperatorId())
      .type(request.getType())
      .reason(request.getReason())
      .expiredAt(request.getExpiredAt())
      .build();
      
      ServerRestriction saved = repository.save(restriction);
      
      outboxService.publish(request.getServerId().toString(), OutboxEventType.SERVER_RESTRICTION_APPLIED, new ServerRestrictionAppliedPayload(restrictionId, request.getServerId(), request.getTargetId(), operatorId, request.getType(), request.getReason(), request.getExpiredAt()));
      
      log.info("Member {} set restriction with type {} to user with id {} for {}", operatorId, request.getType().getDisplayName(), request.getTargetId(), request.getReason());

      return saved;
  }

  @Override
  @Transactional
  public void deactivateRestriction(UUID operatorId, Long restrictionId) {
    ServerRestriction restriction = repository.findById(restrictionId)
      .orElseThrow(() -> new IllegalArgumentException("No restriction with such id: " + restrictionId));
  
    requestAccess(operatorId, restriction.getServerId());
    permissionService.requirePermission(operatorId, restriction.getServerId(), restriction.getType().getPermission());


    restriction.deactivate();

    repository.save(restriction);

    outboxService.publish(restriction.getServerId().toString(), OutboxEventType.SERVER_RESTRICTION_DEACTIVATED, new ServerRestrictionDeactivatedPayload(restrictionId, restriction.getServerId(), restriction.getTargetId(), restriction.getType()));

    log.info("Restriction {} deactivated by operator {} on server {}", restrictionId, operatorId, restriction.getServerId());
  }

  @Override
  @Transactional(readOnly = true)
  public ServerRestriction getRestriction(UUID operatorId, Long restrictionId) {
    ServerRestriction restriction = repository.findById(restrictionId)
      .orElseThrow(() -> new IllegalArgumentException("No restriction with such id: " + restrictionId));

    requestAccess(operatorId, restriction.getServerId());

    return restriction;
  }
  
  @Override
  @Transactional(readOnly = true)
  public List<ServerRestriction> getActiveUserRestrictions(UUID operatorId, UUID targetId, Long serverId) {
    requestAccess(operatorId, serverId);

    List<ServerRestriction> restrictions = repository.findAllByServerIdAndTargetIdAndActiveTrue(serverId, targetId);

    return !restrictions.isEmpty() ? restrictions : List.of();
  }

  @Override
  @Transactional(readOnly = true)
  public Slice<ServerRestriction> getActiveRestrictionsByType(UUID operatorId, Long serverId, ServerRestrictionType type, Pageable pageable) {
    requestAccess(operatorId, serverId);

    return repository.findByServerIdAndTypeAndActiveTrueOrderByIdDesc(serverId, type, pageable);
  }

  @Override
  @Transactional(readOnly = true)
  public Slice<ServerRestriction> getAllUserRestrictions(UUID operatorId, UUID targetId, Long serverId, Pageable pageable) {
    requestAccess(operatorId, serverId);

    return repository.findByServerIdAndTargetIdOrderByIdDesc(serverId, targetId, pageable);
  }

  @Override
  @Transactional(readOnly = true)
  public Slice<ServerRestriction> getServerRestrictionsHistory(UUID operatorId, Long serverId, Pageable pageable) {
    requestAccess(operatorId, serverId);

    return repository.findByServerIdOrderByIdDesc(serverId, pageable);
  }

  @Override
  @Transactional(readOnly = true)
  public boolean isUserBanned(Long serverId, UUID targetId) {
    if (serverId == null || targetId == null) return false;

    return repository.existsByServerIdAndTargetIdAndTypeAndActiveTrue(serverId, targetId, ServerRestrictionType.BAN);
  }
  
  private void requestAccess(UUID operatorId, Long serverId) {
    if (!memberService.checkIsUserMember(serverId, operatorId)) {
      throw new AccessDeniedException("You cannot use this method on server that you not member of!");
    }
  }

  private boolean checkForActiveRestriction(UUID targetId, Long serverId, ServerRestrictionType type) {
    return repository.existsByServerIdAndTargetIdAndTypeAndActiveTrue(serverId, targetId, type);
  }
}
