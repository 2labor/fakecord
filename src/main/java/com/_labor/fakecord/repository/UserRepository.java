package com._labor.fakecord.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com._labor.fakecord.domain.entity.User;

public interface UserRepository extends JpaRepository<User, UUID> {}
