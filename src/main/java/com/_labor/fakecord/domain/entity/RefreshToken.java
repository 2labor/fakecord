package com._labor.fakecord.domain.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import lombok.Builder;

@Entity
@Table(name = "refresh_token")
@Builder
public class RefreshToken {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private long id;
  
  @Column(name = "token", nullable = false, unique = true)
  private String token;

  @OneToOne
  @JoinColumn(name = "account_id", referencedColumnName = "id")
  private Account account;

  @Column(name = "expiry_date", nullable = false)
  private Instant expiryDate;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  public RefreshToken(long id, String token, Account account, Instant expiryDate, Instant createdAt) {
    this.id = id;
    this.token = token;
    this.account = account;
    this.expiryDate = expiryDate;
    this.createdAt = createdAt;
  }

  @PrePersist
  public void onCreate() {
    this.createdAt = Instant.now();
  }

  public RefreshToken(long id, String token, Account account, Instant expiryDate) {
    this.id = id;
    this.token = token;
    this.account = account;
    this.expiryDate = expiryDate;
  }

  public RefreshToken() {
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public Account getAccount() {
    return account;
  }

  public void setAccount(Account account) {
    this.account = account;
  }

  public Instant getExpiryDate() {
    return expiryDate;
  }

  public void setExpiryDate(Instant expiryDate) {
    this.expiryDate = expiryDate;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (int) (id ^ (id >>> 32));
    result = prime * result + ((token == null) ? 0 : token.hashCode());
    result = prime * result + ((expiryDate == null) ? 0 : expiryDate.hashCode());
    result = prime * result + ((createdAt == null) ? 0 : createdAt.hashCode());
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
    RefreshToken other = (RefreshToken) obj;
    if (id != other.id)
      return false;
    if (token == null) {
      if (other.token != null)
        return false;
    } else if (!token.equals(other.token))
      return false;
    if (expiryDate == null) {
      if (other.expiryDate != null)
        return false;
    } else if (!expiryDate.equals(other.expiryDate))
      return false;
    if (createdAt == null) {
      if (other.createdAt != null)
        return false;
    } else if (!createdAt.equals(other.createdAt))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "RefreshToken [id=" + id + ", token=" + token + ", account=" + account + ", expiryDate=" + expiryDate
        + ", createdAt=" + createdAt + "]";
  }

  
}
