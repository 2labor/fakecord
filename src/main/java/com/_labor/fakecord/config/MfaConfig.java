package com._labor.fakecord.config;

import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig;

@Configuration
public class MfaConfig {
  @Bean
  public GoogleAuthenticator googleAuthenticator() {
    GoogleAuthenticatorConfig config = new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder()
      .setTimeStepSizeInMillis(TimeUnit.SECONDS.toMillis(30))
      .setWindowSize(3)
      .build();

    return new GoogleAuthenticator(config);
  }
}
