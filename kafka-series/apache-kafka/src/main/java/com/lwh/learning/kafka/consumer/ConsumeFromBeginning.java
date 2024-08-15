package com.lwh.learning.kafka.consumer;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

/**
 * @author lwh
 * @date 2024-08-14 23:43:32
 * @describe -
 */
public class ConsumeFromBeginning {

    private final static Logger LOGGER = LoggerFactory.getLogger(ConsumeFromBeginning.class);

    public static void main(String[] args) {
        setUp();
    }

    /**
     * 消费的简单事例
     */
    private static void setUp() {
        Properties consumerProperties = new Properties();
        consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, "myFirstConsumer");
        try (Consumer<Long, String> consumer = new KafkaConsumer<>(consumerProperties)) {
            String topic = "my-first-topic";
            consumer.subscribe(List.of(topic));
            consumer.poll(Duration.ofMinutes(1))
                    .forEach(System.out::println);
        }
    }
}
