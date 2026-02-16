package com._labor.fakecord.domain.entity;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileMetadata {
  private String theme;
  private List<String> badges = new ArrayList<>();
  private boolean isNitro = false;
  private String customStatus;
}
