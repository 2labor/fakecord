package com._labor.fakecord.domain.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;

@Entity
@Table(name = "verification_tokens")
@Builder
public class VerificationToken {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "token", nullable = false)
  private String token;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false)
  private TokenType type;

  @Column(name = "expiring_date", nullable = false)
  private Instant expiringDate;

  @Builder.Default
  @Column(name = "attempts", nullable = false)
  private int attempts = 0;

  @Column(name = "ip_address")
  private String ipAddress;

  @Column(name = "user_agent")
  private String userAgent;


  public VerificationToken(UUID id, String token, User user, TokenType type, Instant expiringDate, int attempts,
      String ipAddress, String userAgent) {
    this.id = id;
    this.token = token;
    this.user = user;
    this.type = type;
    this.expiringDate = expiringDate;
    this.attempts = attempts;
    this.ipAddress = ipAddress;
    this.userAgent = userAgent;
  }

  public VerificationToken(String token, User user, TokenType type, Instant expiringDate, String ipAddress,
      String userAgent) {
    this.token = token;
    this.user = user;
    this.type = type;
    this.expiringDate = expiringDate;
    this.ipAddress = ipAddress;
    this.userAgent = userAgent;
  }

  public VerificationToken() {
  }

  public boolean isExpired() {
    return Instant.now().isAfter(this.expiringDate);
  }

  public void incrementAttempts() {
    this.attempts++;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public TokenType getType() {
    return type;
  }

  public void setType(TokenType type) {
    this.type = type;
  }

  public Instant getExpiringDate() {
    return expiringDate;
  }

  public void setExpiringDate(Instant expiringDate) {
    this.expiringDate = expiringDate;
  }

  public int getAttempts() {
    return attempts;
  }

  public void setAttempts(int attempts) {
    this.attempts = attempts;
  }

  public String getIpAddress() {
    return ipAddress;
  }

  public void setIpAddress(String ipAddress) {
    this.ipAddress = ipAddress;
  }

  public String getUserAgent() {
    return userAgent;
  }

  public void setUserAgent(String userAgent) {
    this.userAgent = userAgent;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((token == null) ? 0 : token.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    result = prime * result + ((expiringDate == null) ? 0 : expiringDate.hashCode());
    result = prime * result + attempts;
    result = prime * result + ((ipAddress == null) ? 0 : ipAddress.hashCode());
    result = prime * result + ((userAgent == null) ? 0 : userAgent.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    VerificationToken other = (VerificationToken) obj;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    if (token == null) {
      if (other.token != null)
        return false;
    } else if (!token.equals(other.token))
      return false;
    if (type != other.type)
      return false;
    if (expiringDate == null) {
      if (other.expiringDate != null)
        return false;
    } else if (!expiringDate.equals(other.expiringDate))
      return false;
    if (attempts != other.attempts)
      return false;
    if (ipAddress == null) {
      if (other.ipAddress != null)
        return false;
    } else if (!ipAddress.equals(other.ipAddress))
      return false;
    if (userAgent == null) {
      if (other.userAgent != null)
        return false;
    } else if (!userAgent.equals(other.userAgent))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "VerificationToken [id=" + id + ", token=" + token + ", type=" + type + ", expiringDate=" + expiringDate
        + ", attempts=" + attempts + ", ipAddress=" + ipAddress + ", userAgent=" + userAgent + "]";
  }
}
