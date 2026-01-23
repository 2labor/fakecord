package com._labor.fakecord.utils;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EncryptionUtil {

  private final String ALGORITHM = "AES/CBC/PKCS5Padding";
  private final int IV_SIZE = 16;
  
  @Value("${fakecord.mfa.encryption-key}")
  private String secretKey;

  public String encrypt(String data) {
    try {
      byte[] iv = new byte[IV_SIZE];
      new SecureRandom().nextBytes(iv);
      IvParameterSpec ivSpec = new IvParameterSpec(iv);

      SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(), "AES");
      Cipher cipher = Cipher.getInstance(ALGORITHM);
      cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

      byte[] encrypted = cipher.doFinal(data.getBytes());

      byte[] combined = ByteBuffer.allocate(iv.length + encrypted.length)
        .put(iv)
        .put(encrypted)
        .array();
      
      return Base64.getEncoder().encodeToString(combined);
    } catch (Exception e) {
      throw new RuntimeException("Error while encoding data: " + e.getMessage());
    }
  }

  public String decrypt(String encryptedDataWithIv) {
    try {
      byte[] combined = Base64.getDecoder().decode(encryptedDataWithIv);
      ByteBuffer buffer = ByteBuffer.wrap(combined);

      byte[] iv = new byte[IV_SIZE];
      buffer.get(iv);
      IvParameterSpec ivSec = new IvParameterSpec(iv);

      byte[] encrypted = new byte[buffer.remaining()];
      buffer.get(encrypted);

      SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(), "AES");
      Cipher cipher = Cipher.getInstance(ALGORITHM);
      cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSec);

      return new String(cipher.doFinal(encrypted));
    } catch (Exception e) {
      throw new RuntimeException("Error while description data: " + e.getMessage());
    }
  }
}
