package com._labor.fakecord.domain.dto;

import com._labor.fakecord.domain.enums.ConnectionProvider;

public record ConnectionStatusDto(
  ConnectionProvider provider,
  boolean connected,
  String externalId,
  String externalName,
  boolean showOnProfile
) {}