package com._labor.fakecord.domain.enums;

public enum MediaType {
  AVATAR("avatars"),
  BANNER("banners");

  private final String folder;

  MediaType(String folder) {
    this.folder = folder;
  }

  public String getFolder() {
    return folder;
  }
}
