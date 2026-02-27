package com._labor.fakecord.domain.mappper.Impl;

import com._labor.fakecord.domain.dto.ConnectionDto;
import com._labor.fakecord.domain.entity.UserConnection;
import com._labor.fakecord.domain.mappper.ConnectionMapper;

public class ConnectionMapperImpl implements ConnectionMapper{

  @Override
  public ConnectionDto toDto(UserConnection entity) {
    if (null == entity) return null;

    return ConnectionDto.builder()
      .provider(entity.getProvider())
      .externalName(entity.getExternalName())
      .showOnProfile(entity.isShowOnProfile())
      .build();
  }
  
}
