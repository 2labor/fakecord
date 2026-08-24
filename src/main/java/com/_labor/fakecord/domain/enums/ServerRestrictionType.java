package com._labor.fakecord.domain.enums;

public enum ServerRestrictionType {
  KICK("kick", "KICK_MEMBERS"), 
  BAN("ban", "BAN_MEMBERS"),
  TIMEOUT("timeout", "TIMEOUT_MEMBERS");

  private final String displayName;
  private final String requiredPermission;

  ServerRestrictionType(String displayName, String requiredPermission) {
    this.displayName = displayName;
    this.requiredPermission = requiredPermission;
  }

  public String getDisplayName() {
    return displayName;
  }

  public String getPermissionString() {
    return requiredPermission;
  }

  public ServerRolePermissions getPermission() {
    return ServerRolePermissions.valueOf(requiredPermission);
  }
}