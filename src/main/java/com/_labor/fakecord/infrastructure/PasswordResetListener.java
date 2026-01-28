package com._labor.fakecord.infrastructure;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com._labor.fakecord.domain.events.PasswordResetRequestedEvent;
import com._labor.fakecord.services.MailService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PasswordResetListener {
  private final MailService mailService;

  public PasswordResetListener(MailService mailService) {
    this.mailService = mailService;
  }

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handlePasswordResetRequest(PasswordResetRequestedEvent event) {
    log.debug("Listener caught PasswordResetRequestedEvent for {}", event.getEmail());

    String subject = "Fakecord: Password Reset Request";
    String resetUrl = "http://localhost:8080/?token=" + event.getToken();
    String message = "To reset your password, click the link: " + resetUrl;

    mailService.sendMain(event.getEmail(), subject, message);
  }
}
