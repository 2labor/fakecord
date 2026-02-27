package com._labor.fakecord.domain.mappper;

import com._labor.fakecord.domain.dto.ConnectionDto;
import com._labor.fakecord.domain.entity.UserConnection;

public interface ConnectionMapper {
  ConnectionDto toDto(UserConnection entity);
}
