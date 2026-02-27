package com._labor.fakecord.domain.dto;

import com._labor.fakecord.domain.enums.ConnectionProvider;

import lombok.Builder;

@Builder
public record ConnectionDto(
  ConnectionProvider provider,
  String externalName,
  String externalId,
  boolean showOnProfile  
) {}