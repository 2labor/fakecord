package com._labor.fakecord.services.impl;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com._labor.fakecord.services.MailService;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SmtpMailService implements MailService {

  private final JavaMailSender mailSender;
  private final TemplateEngine templateEngine;

  public SmtpMailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
    this.mailSender = mailSender;
    this.templateEngine = templateEngine;
  }

  @Override
  public void sendMain(String to, String subject, String body) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom("Fakecord <beehappyyyyyyy@gmail.com>");
    message.setTo(to);
    message.setSubject(subject);
    message.setText(body);
    mailSender.send(message);
  }

  @Async
  @Override
  public void sendDiscordStyleConfirmation(String to, String username, String token) {
    log.debug("Preparing Discord-style HTML email for {}", to);

    try {
      Context context = new Context();
      context.setVariable("username", username);
      context.setVariable("confirmUrl", "http://localhost:8080/api/auth/verify/email?token=" + token);
      String htmlContent = templateEngine.process("email/verification", context);

      MimeMessage mimeMessage = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

      helper.setFrom("Fakecord <beehappyyyyyyy@gmail.com>");
      helper.setTo(to);
      helper.setSubject("Verify Email Address for Fakecord");
      helper.setText(htmlContent, true);

      mailSender.send(mimeMessage);
      log.info("HTML Verification email successfully sent to {}", to);
      
    } catch (Exception e) {
      log.error("Failed to send HTML email to {}. Error: {}", to, e.getMessage());
    }
  }
}