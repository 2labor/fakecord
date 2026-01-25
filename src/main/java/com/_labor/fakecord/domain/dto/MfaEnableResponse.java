package com._labor.fakecord.domain.dto;

import java.util.List;


public record MfaEnableResponse(
  String message,
  List<String> backupCodes
) {}