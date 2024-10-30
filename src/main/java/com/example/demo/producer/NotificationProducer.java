package com.example.demo.producer;

import com.example.demo.Notification;
import com.example.demo.utils.RabbitMQConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.util.JsonFormat;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationProducer {
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public NotificationProducer(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendMessage(Notification notification) {
        try {
            String message = JsonFormat.printer().print(notification);
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY, message);
            System.out.println("Message sent: " + message);
        } catch (Exception e) {
            System.err.println("Error serializing message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}