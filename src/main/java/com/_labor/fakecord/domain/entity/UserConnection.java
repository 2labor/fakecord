package com._labor.fakecord.domain.entity;

import java.time.Instant;
import java.util.UUID;

import com._labor.fakecord.domain.enums.ConnectionProvider;
import com._labor.fakecord.infrastructure.persistence.converter.EncryptionConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_connections", uniqueConstraints = {
    @UniqueConstraint(name = "uk_user_provider", columnNames = {"user_id", "provider"})
})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserConnection {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "provider", nullable = false)
  @Enumerated(EnumType.STRING)
  private ConnectionProvider provider;

  @Column(name = "external_id", nullable = false)
  private String externalId;

  @Column(name = "external_name", nullable = false)
  private String externalName;

  @Column(name = "access_token", columnDefinition = "TEXT")
  @Convert(converter = EncryptionConverter.class)
  private String accessToken;

  @Column(name = "refresh_token", columnDefinition = "TEXT")
  @Convert(converter = EncryptionConverter.class)
  private String refreshToken;

  @Column(name = "expires_at")
  private Instant expiresAt;

  @Column(name = "show_on_profile")
  private boolean showOnProfile = true;

  @Column(name = "metadata", columnDefinition = "jsonb")
  private String metadata;

  @Column(name = "created_at")
  private Instant createdAt;

  @PrePersist
  public void onCreate() {
    this.createdAt = Instant.now();
  } 
}
