package com._labor.fakecord.services.impl;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com._labor.fakecord.services.MailService;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class SmtpMailService implements MailService {

  private final JavaMailSender mailSender;

  public SmtpMailService(JavaMailSender mailSender) {
    this.mailSender = mailSender;
  }

  @Override
  public void sendMain(String to, String subject, String body) {
    log.debug("Preparing to send email to {}", to);

    try {
      SimpleMailMessage message = new SimpleMailMessage();

      message.setFrom("FakeCode <beehappyyyyyyy@gmail.com>");
      message.setTo(to);
      message.setSubject(subject);
      message.setText(body);

      mailSender.send(message);
      log.info("Email successfully sent to {}", to);
    } catch (Exception e) {
      log.error("Failed to send email to {}. Reason: {}", to, e.getMessage());
    }
  }
  
}
