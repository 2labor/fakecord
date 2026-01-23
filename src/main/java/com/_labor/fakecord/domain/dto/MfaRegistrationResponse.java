package com._labor.fakecord.domain.dto;

public record MfaRegistrationResponse(
  String secret,
  String qrCodeUrl
) {}