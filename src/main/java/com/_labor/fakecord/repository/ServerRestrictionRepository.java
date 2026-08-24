package com._labor.fakecord.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com._labor.fakecord.domain.entity.ServerRestriction;
import com._labor.fakecord.domain.enums.ServerRestrictionType;

public interface ServerRestrictionRepository extends JpaRepository<ServerRestriction, Long>{
  boolean existsByServerIdAndTargetIdAndTypeAndActiveTrue(Long serverId, UUID targetId, ServerRestrictionType type);
  Optional<ServerRestriction> findByServerIdAndTargetIdAndTypeAndActiveTrue(Long serverId, UUID targetId, ServerRestrictionType type);
  Slice<ServerRestriction> findByServerIdOrderByIdDesc(Long serverId, Pageable pageable);
  
  List<ServerRestriction> findAllByServerIdAndTargetIdAndActiveTrue(Long serverId, UUID targetId);

  @Modifying
  @Query("UPDATE ServerRestriction r SET r.active = false WHERE r.serverId = :serverId AND r.targetId = :targetId AND r.type = :type AND r.active = true")
  int deactivateRestriction(Long serverId, UUID targetId, ServerRestrictionType type); 

  Slice<ServerRestriction> findByServerIdAndTypeAndActiveTrueOrderByIdDesc(Long serverId, ServerRestrictionType type, Pageable pageable);

  Slice<ServerRestriction> findByServerIdAndActiveTrueOrderByIdDesc(Long serverId, Pageable pageable);

  Slice<ServerRestriction> findByServerIdAndTargetIdOrderByIdDesc(Long serverId, UUID targetId, Pageable pageable);
}