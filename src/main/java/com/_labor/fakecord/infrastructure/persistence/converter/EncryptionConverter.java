package com._labor.fakecord.infrastructure.persistence.converter;

import com._labor.fakecord.utils.EncryptionUtil;

import jakarta.persistence.AttributeConverter;

public class EncryptionConverter implements AttributeConverter<String, String> {

  private final EncryptionUtil encryptionUtil;

  public EncryptionConverter(EncryptionUtil encryptionUtil) {
    this.encryptionUtil = encryptionUtil;
  }

  @Override
  public String convertToDatabaseColumn(String attribute) {
    if (null == attribute) return null;
    return encryptionUtil.encrypt(attribute);
  }

  @Override
  public String convertToEntityAttribute(String dbData) {
    if (null == dbData) return null;
    return encryptionUtil.decrypt(dbData);
  }


}
