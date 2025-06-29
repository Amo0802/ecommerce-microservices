package com.ecommerce.user_service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    private static final String EXCHANGE_NAME = "user-events";

    public void publishUserRegisteredEvent(UserEvent event) {
        event.setEventType("USER_REGISTERED");
        event.setTimestamp(LocalDateTime.now());

        log.info("Publishing user registered event: {}", event);
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, "user.registered", event);
    }

    public void publishEmailVerifiedEvent(UserEvent event) {
        event.setEventType("EMAIL_VERIFIED");
        event.setTimestamp(LocalDateTime.now());

        log.info("Publishing email verified event: {}", event);
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, "user.email.verified", event);
    }
}