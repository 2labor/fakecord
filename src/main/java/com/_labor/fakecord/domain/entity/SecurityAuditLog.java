package com._labor.fakecord.domain.entity;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com._labor.fakecord.domain.model.AuditMetadata;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "security_audit_logs", indexes = {
  @Index(name = "idx_audit_user_created", columnList = "user_id, created_at")
})
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE) 
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SecurityAuditLog {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;  

  @Column(name = "action")
  @Enumerated(EnumType.STRING)
  private AuditAction type;

  @ManyToOne (fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @Column(name = "ip_address")
  private String ipAddress;

  @Column(name = "agent")
  private String agent;

  @Column(name = "os")
  private String os;

  @Column(name = "device_type")
  private String deviceType;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "metadata", columnDefinition = "jsonb")
  private AuditMetadata metadata;
  
  @Column(name = "created_at", updatable = false, nullable = false)
  private Instant createdAt;

  @PrePersist
  public void onCreate() {
    this.createdAt = Instant.now();
  }

  public UUID getId() {
    return id;
  }

  public AuditAction getType() {
    return type;
  }

  public User getUser() {
    return user;
  }

  public String getIpAddress() {
    return ipAddress;
  }

  public String getAgent() {
    return agent;
  }

  public String getOs() {
    return os;
  }

  public String getDeviceType() {
    return deviceType;
  }

  public AuditMetadata getMetadata() {
    return metadata;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}
