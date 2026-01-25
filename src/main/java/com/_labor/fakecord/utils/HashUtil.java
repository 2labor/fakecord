package com._labor.fakecord.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

public class HashUtil {
  public static String hashSha256(String input) {
    try {
      var digest = MessageDigest.getInstance("SHA-256");
      byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
      return HexFormat.of().formatHex(hash);
    } catch (Exception e) {
      throw new RuntimeException("Fatal error: SHA-256 algorithm not found", e);
    }
  }
}
