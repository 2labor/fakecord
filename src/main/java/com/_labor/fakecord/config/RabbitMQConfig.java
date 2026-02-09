package com._labor.fakecord.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
  public static final String DELAYED_EXCHANGE = "registration-delayed-exchange";
  public static final String QUEUE_NAME = "registration-expiry-queue";
  public static final String ROUTING_KEY = "registration.unconfirmed";

  @Bean
  public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
      return new RabbitAdmin(connectionFactory);
  }

  @Bean
  public CustomExchange delayExchange() {
    Map<String, Object> args = new HashMap<>();
    args.put("x-delayed-type", "direct");
    return new CustomExchange(DELAYED_EXCHANGE, "x-delayed-message", true, false, args);
  }

  @Bean
  public Queue registrationQueue() {
    return new Queue(QUEUE_NAME, true);
  }

  @Bean
  public Binding binding(Queue registrationQueue, CustomExchange delayExchange) { 
    return BindingBuilder.bind(registrationQueue)
      .to(delayExchange)
      .with(ROUTING_KEY)
      .noargs();
  }
  
  @Bean
  public ApplicationRunner initializeRabbit(RabbitAdmin rabbitAdmin) {
    return args -> {
      rabbitAdmin.initialize();
    };
  }
}
