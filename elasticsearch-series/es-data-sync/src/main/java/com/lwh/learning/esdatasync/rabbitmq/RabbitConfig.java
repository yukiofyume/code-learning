package com.lwh.learning.esdatasync.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lwh
 * @date 2025-02-26 20:54:37
 * @describe -
 */
@Configuration
public class RabbitConfig {

    /**
     * 防止无法序列化
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public FanoutExchange alarmDirectExchange() {
        return new FanoutExchange("alarm.exchange");
    }

    @Bean
    public Queue alarmQueue() {
        return QueueBuilder.durable("alarm.queue")
                // 声明和当前队列绑定的死信交换机
                .withArgument("x-dead-letter-exchange", "dead.letter.exchange")
                // 声明和当前队列绑定的死信路由key
                .withArgument("x-dead-letter-routing-key", "dead.letter.key")
                .build();
    }

    @Bean
    public Binding bindingAlarmQueue() {
        return BindingBuilder.bind(alarmQueue())
                .to(alarmDirectExchange());
    }

    /**
     * 死信交换机
     */
    @Bean
    public DirectExchange deadLetterExchange() {
        return ExchangeBuilder.directExchange("dead.letter.exchange").durable(true).build();
    }

    /**
     * 死信队列
     */
    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable("dead.letter.queue").build();
    }

    /**
     * 死信绑定关系
     */
    @Bean
    public Binding bindingDeadLetterQueue() {
        return BindingBuilder.bind(deadLetterQueue())
                .to(deadLetterExchange())
                .with("dead.letter.key");
    }
}
