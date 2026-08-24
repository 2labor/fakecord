package com._labor.fakecord.domain.entity;

import java.time.Instant;
import java.util.UUID;

import com._labor.fakecord.domain.enums.ServerRestrictionType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "server_restrictions",
  indexes = {
    @Index(name = "idx_restrictions_server_history", columnList = "server_id, id DESC"),
    @Index(name = "idx_restrictions_target_active", columnList = "server_id, target_id, type")
  }
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ServerRestriction {
  
  @Id
  private Long id;

  @Column(name = "server_id", nullable = false, updatable = false)
  private Long serverId;

  @Column(name = "target_id", nullable = false, updatable = false)
  private UUID targetId;

  @Column(name = "operator_id", nullable = false, updatable = false)
  private UUID operatorId;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false)
  private ServerRestrictionType type;

  @Column(name = "reason")
  private String reason;

  @Column(name = "is_active", nullable = false)
  private boolean active;

  @Column(name = "expires_at")
  private Instant expiredAt;

  @Column(name = "created_at", nullable = false, updatable = false) 
  private Instant createdAt;

  @Builder
  public ServerRestriction(
    Long id,
    Long serverId,
    UUID targetId,
    UUID operatorId,
    String reason,
    ServerRestrictionType type,
    Instant expiredAt
  ) {
    this.id = id;
    this.serverId = serverId;
    this.targetId = targetId;
    this.operatorId = operatorId;
    this.reason = reason;
    this.type = type;
    this.expiredAt = expiredAt;
  }

  @PrePersist
  public void onCreate() {
    this.createdAt = Instant.now();
    this.active = true;
  }

  public void deactivate() {
    this.active = false;
  }

  public boolean isExpired() {
    return expiredAt != null && expiredAt.isBefore(Instant.now());
  }
}