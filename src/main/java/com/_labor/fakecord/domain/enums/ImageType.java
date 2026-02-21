package com._labor.fakecord.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ImageType {
    JPG("image/jpeg"),
    PNG("image/png"),
    WEBP("image/webp"),
    GIF("image/gif");

    private final String mimeType;
}