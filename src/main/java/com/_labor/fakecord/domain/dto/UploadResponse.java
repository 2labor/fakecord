package com._labor.fakecord.domain.dto;

import lombok.Builder;

@Builder
public record UploadResponse(
  String uploadUrl,
  String publicUrl
) {}