package com._labor.fakecord.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class UserSecuritySettings {
  @Column(name = "token_version", nullable = false)
  private int tokenVersion= 0;

  public UserSecuritySettings(int tokenVersion) {
    this.tokenVersion = tokenVersion;
  }

  public UserSecuritySettings() {
  }

  public int getTokenVersion() {
    return tokenVersion;
  }

  public void setTokenVersion(int tokenVersion) {
    this.tokenVersion = tokenVersion;
  }

  public void incrementTokenVersion() {
    this.tokenVersion++;
  }
}
