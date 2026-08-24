package com._labor.fakecord.controller;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com._labor.fakecord.domain.dto.ServerRestrictionResponse;
import com._labor.fakecord.domain.entity.ServerRestriction;
import com._labor.fakecord.domain.enums.ServerRestrictionType;
import com._labor.fakecord.domain.enums.ServerRolePermissions;
import com._labor.fakecord.domain.mappper.ServerRestrictionMapper;
import com._labor.fakecord.domain.request.CreateServerRestrictionRequest;
import com._labor.fakecord.security.permissions.RequirePermission;
import com._labor.fakecord.services.ServerRestrictionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/servers/{serverId}/restrictions")
@RequiredArgsConstructor
@Slf4j
public class ServerRestrictionController {
  
  private final ServerRestrictionService service;
  private final ServerRestrictionMapper mapper;

  @PostMapping
  @RequirePermission(permissionSpel = "#request.type.getPermissionString()")
  public ResponseEntity<ServerRestrictionResponse> setRestriction(
    Principal principal,
    @PathVariable Long serverId,
    @Valid @RequestBody CreateServerRestrictionRequest request
  ) {
    UUID operatorId = getUserId(principal);

    request.setOperatorId(operatorId);
    request.setServerId(serverId);
    
    ServerRestriction restriction = service.setRestriction(operatorId, mapper.fromRequest(request));

    return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDto(restriction));
  }

  @DeleteMapping("/{restrictionId}")
  public ResponseEntity<Void> deactivateRestriction(
    Principal principal,
    @PathVariable Long serverId,
    @PathVariable Long restrictionId
  ) {
    UUID operatorId = getUserId(principal);

    service.deactivateRestriction(operatorId, restrictionId);

    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{restrictionId}")
  @RequirePermission(ServerRolePermissions.MANAGE_RESTRICTIONS)
  public ResponseEntity<ServerRestrictionResponse> getRestriction(
    Principal principal,
    @PathVariable Long serverId,
    @PathVariable Long restrictionId
  ) {
    UUID operatorId = getUserId(principal);

    ServerRestriction restriction = service.getRestriction(operatorId, restrictionId);

    return ResponseEntity.ok(mapper.toDto(restriction));
  }

  @GetMapping("/{targetId}/active")
  @RequirePermission(ServerRolePermissions.VIEW_AUDIT_LOG)
  public ResponseEntity<List<ServerRestrictionResponse>> getUserActiveRestrictions(
    Principal principal,
    @PathVariable Long serverId,
    @PathVariable UUID targetId
  ) {
    UUID operatorId = getUserId(principal);

    List<ServerRestriction> restrictions = service.getActiveUserRestrictions(operatorId, targetId, serverId);

    return ResponseEntity.ok(mapper.toListDto(restrictions));
  }

  @GetMapping("/{targetId}/history")
  @RequirePermission(ServerRolePermissions.VIEW_AUDIT_LOG)
  public ResponseEntity<Slice<ServerRestrictionResponse>> getAllUsersRestrictions(
    Principal principal,
    @PathVariable Long serverId,
    @PathVariable UUID targetId,
    @PageableDefault(size = 20) Pageable pageable
  ) {
    UUID operatorId = getUserId(principal);

    Slice<ServerRestriction> restrictions = service.getAllUserRestrictions(operatorId, targetId, serverId, pageable);

    return ResponseEntity.ok(mapper.toSliceDto(restrictions));
  }

  @GetMapping("/active")
  @RequirePermission(ServerRolePermissions.VIEW_AUDIT_LOG)
  public ResponseEntity<Slice<ServerRestrictionResponse>> getAllActiveRestrictionsWithType(
    Principal principal,
    @RequestParam ServerRestrictionType type,
    @PathVariable Long serverId,
    @PageableDefault(size = 20) Pageable pageable
  ) {
    UUID operatorId = getUserId(principal);

    Slice<ServerRestriction> restrictions = service.getActiveRestrictionsByType(operatorId, serverId, type, pageable);

    return ResponseEntity.ok(mapper.toSliceDto(restrictions));
  }

  @GetMapping("/history")
  @RequirePermission(ServerRolePermissions.VIEW_AUDIT_LOG)
  public ResponseEntity<Slice<ServerRestrictionResponse>> getHistory(
    Principal principal,
    @PathVariable Long serverId,
    @PageableDefault(size = 20) Pageable pageable
  ) {
    UUID operatorId = getUserId(principal);

    Slice<ServerRestriction> restriction = service.getServerRestrictionsHistory(operatorId, serverId, pageable);

    return ResponseEntity.ok(mapper.toSliceDto(restriction));
  }

  UUID getUserId(Principal principal) {
    if (principal == null) throw new AccessDeniedException("You have to be login for using this method!");

    return UUID.fromString(principal.getName().toString());
  }

}
