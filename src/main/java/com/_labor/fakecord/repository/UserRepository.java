package com._labor.fakecord.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com._labor.fakecord.domain.entity.User;

public interface UserRepository extends JpaRepository<User, UUID> {
  Optional<User> findByName(String name);
  boolean existsByName(String name);
  @Query("SELECT u FROM User u " +
           "LEFT JOIN u.account a " +
           "WHERE a.email = :email")
    Optional<User> findByEmailAcrossAllProviders(@Param("email") String email);
}
