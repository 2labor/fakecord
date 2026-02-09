package com._labor.fakecord.services;

public interface RegistrationExpiryConsumer {
  void handleExpiry(String message);
}
