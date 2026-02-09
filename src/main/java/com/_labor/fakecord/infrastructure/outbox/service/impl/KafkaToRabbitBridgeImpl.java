package com._labor.fakecord.infrastructure.outbox.service.impl;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com._labor.fakecord.config.RabbitMQConfig;
import com._labor.fakecord.infrastructure.outbox.service.EventBridge;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class KafkaToRabbitBridgeImpl implements EventBridge {

  private final RabbitTemplate template;

  @Value("${app.registration.expiry-delay}")
  private long expiryDelay;

  public KafkaToRabbitBridgeImpl(RabbitTemplate template) {
    this.template = template;
  }

  @Override
  @KafkaListener(topics = "user-registration-events", groupId = "bridge-group")
  public void process(String message) {
    log.info("Bridge Implementation: Processing event from Kafka...");

     try {
      template.convertAndSend(
        RabbitMQConfig.DELAYED_EXCHANGE,
        RabbitMQConfig.ROUTING_KEY,
        message,
        msg -> {
          msg.getMessageProperties().setHeader("x-delay", expiryDelay);
            return msg;
        }
      );
      log.info("Bridge Implementation: Message sent to RabbitMQ exchange");
     } catch (Exception e) {
        log.error("❌ Bridge Implementation: Error during message forwarding", e);
        throw e;
     }
  }
  
}
