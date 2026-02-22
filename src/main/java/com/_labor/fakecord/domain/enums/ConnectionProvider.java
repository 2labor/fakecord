package com._labor.fakecord.domain.enums;

public enum ConnectionProvider {
  SPOTIFY("spotify"),
  STEAM("steam"),
  GITHUB("gitHub");

  private final String displayName;

  ConnectionProvider(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }
}
