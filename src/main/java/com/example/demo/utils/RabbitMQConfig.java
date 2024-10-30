package com.example.demo.utils;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_NAME = "myQueue";
    public static final String EXCHANGE_NAME = "notification-topic";
    public static final String ROUTING_KEY = "my.routing.key";

    @Bean
    public Queue myQueue() {
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange notificationExchange) {
        return BindingBuilder.bind(queue).to(notificationExchange).with(ROUTING_KEY);
    }
}