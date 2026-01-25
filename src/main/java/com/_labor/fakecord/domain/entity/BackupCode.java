package com._labor.fakecord.domain.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import lombok.Setter;

@Entity
@Table(name = "backup_codes", indexes = {
  @Index(name = "user_id_prefix",columnList = "user_id, prefix_hash")
})
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BackupCode {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @Column(name = "prefix_hash", nullable = false, length = 64)
  private String prefixHash;

  @Column(name = "hash_code", nullable = false)
  private String hashCode;

  @Setter
  @Column(name = "used_at")
  private Instant usedAt;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @PrePersist
  public void onCreate() {
    this.createdAt = Instant.now();
  }

  public void use() {
    this.usedAt = Instant.now();
  }

  public UUID getId() {
    return id;
  }

  public User getUser() {
    return user;
  }

  public String getPrefixHash() {
    return prefixHash;
  }

  public String getHashCode() {
    return hashCode;
  }

  public Instant getUsedAt() {
    return usedAt;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}
