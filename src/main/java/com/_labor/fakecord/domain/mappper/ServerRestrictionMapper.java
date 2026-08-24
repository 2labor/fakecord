package com._labor.fakecord.domain.mappper;

import java.util.List;

import org.springframework.data.domain.Slice;

import com._labor.fakecord.domain.dto.ServerRestrictionResponse;
import com._labor.fakecord.domain.entity.ServerRestriction;
import com._labor.fakecord.domain.request.CreateServerRestrictionRequest;

public interface ServerRestrictionMapper {
  ServerRestriction fromRequest(CreateServerRestrictionRequest request);
  ServerRestrictionResponse toDto(ServerRestriction entity);
  List<ServerRestrictionResponse> toListDto(List<ServerRestriction> entities);
  Slice<ServerRestrictionResponse> toSliceDto(Slice<ServerRestriction> sliceEntity);
}
