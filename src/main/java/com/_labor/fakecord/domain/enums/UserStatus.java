package com._labor.fakecord.domain.enums;

import lombok.Getter;

@Getter
public enum UserStatus {
  ONLINE("online"),
  IDLE("idle"),
  DO_NOT_DISTURB("dnd"),
  INVISIBLE("invisible"),
  OFFLINE("offline");

  private final String value;

  UserStatus(String value) {
    this.value = value;
  }
}
