package com.patika.kredinbizdeservice.producer;

import com.patika.kredinbizdeservice.configuration.RabbitMQConfiguration;
import com.patika.kredinbizdeservice.producer.dto.NotificationDTO;
import com.patika.kredinbizdeservice.producer.enums.LogType;
import com.patika.kredinbizdeservice.producer.enums.SuccessType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationProducer {

    // private final RabbitTemplate rabbitTemplate;

    private final RabbitMQConfiguration rabbitMQConfiguration;
    private final AmqpTemplate amqpTemplate;

    public void sendNotification(NotificationDTO notificationDTO) {
        log.info("notification sent: {}", notificationDTO);
        amqpTemplate.convertSendAndReceive(rabbitMQConfiguration.getExchange(), rabbitMQConfiguration.getRoutingkey(), notificationDTO);
    }

    public NotificationDTO prepareNotificationDTO(LogType logType, SuccessType successType, String dbName, String message) {
        return NotificationDTO.builder()
                .message(message)
                .logType(logType)
                .successType(successType)
                .time(LocalDateTime.now())
                .databaseName(dbName)
                .build();
    }
}
