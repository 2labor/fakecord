package com._labor.fakecord.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com._labor.fakecord.domain.entity.User;
import com._labor.fakecord.domain.entity.UserConnection;
import com._labor.fakecord.domain.enums.ConnectionProvider;

public interface UserConnectionRepository extends JpaRepository<UserConnection, UUID>{
  Optional<UserConnection> findByUserAndProvider(User user, ConnectionProvider provider);
  List<UserConnection> findAllByUserIdInAndProvider(List<UUID> userIds, ConnectionProvider provider);
}
