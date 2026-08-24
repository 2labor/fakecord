package com._labor.fakecord.domain.mappper.Impl;

import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Component;

import com._labor.fakecord.domain.dto.ServerRestrictionResponse;
import com._labor.fakecord.domain.entity.ServerRestriction;
import com._labor.fakecord.domain.mappper.ServerRestrictionMapper;
import com._labor.fakecord.domain.request.CreateServerRestrictionRequest;

@Component
public class ServerRestrictionMapperImpl implements ServerRestrictionMapper {

  @Override
  public ServerRestriction fromRequest(CreateServerRestrictionRequest request) {
    if (request == null) return null;

    return ServerRestriction.builder()
      .serverId(request.getServerId())
      .targetId(request.getTargetId())
      .operatorId(request.getOperatorId())
      .reason(request.getReason())
      .type(request.getType())
      .expiredAt(request.getExpiredAt())
      .build();
  }

  @Override
  public ServerRestrictionResponse toDto(ServerRestriction entity) {
    if (entity == null) return null;

    return ServerRestrictionResponse.builder()
      .id(entity.getId())
      .serverId(entity.getServerId())
      .operatorId(entity.getOperatorId())
      .targetId(entity.getTargetId())
      .type(entity.getType().getDisplayName())
      .reason(entity.getReason())
      .active(entity.isActive())
      .expiredAt(entity.getExpiredAt() != null ? entity.getExpiredAt().toEpochMilli() : null)
      .createdAt(entity.getCreatedAt().toEpochMilli())
      .build();
  }

  @Override
  public List<ServerRestrictionResponse> toListDto(List<ServerRestriction> entities) {
    if (entities == null || entities.isEmpty()) return List.of();

    return entities.stream().map(this::toDto).toList();
  }

  @Override
  public Slice<ServerRestrictionResponse> toSliceDto(Slice<ServerRestriction> sliceEntity) {
    if (sliceEntity == null || sliceEntity.isEmpty()) return new SliceImpl<>(Collections.emptyList());
    
    return sliceEntity.map(this::toDto);
  }
}
