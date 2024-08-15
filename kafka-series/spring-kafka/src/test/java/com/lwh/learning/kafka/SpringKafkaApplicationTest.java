package com.lwh.learning.kafka;

import com.lwh.learning.kafka.model.dto.Farewell;
import com.lwh.learning.kafka.model.dto.GreetingDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * @author lwh
 * @date 2024-08-15 20:10:31
 * @describe -
 */
@SpringBootTest
class SpringKafkaApplicationTest {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private KafkaTemplate<String, GreetingDTO> greetingDTOKafkaTemplate;

    @Autowired
    private KafkaTemplate<String, Object> multiObjectKafkaTemplate;

    @Test
    void sendMessageTest() {
        kafkaTemplate.send("hwlTest", "hello world");
    }

    @Test
    void sendMessageTest2() {
        String message = "sendMessageTest2";
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send("hwlTest", message);
        future.whenComplete((result, ex) -> {
            if (Objects.isNull(ex)) {
                System.out.println("Sent message=[" + message + "] with offset=[" + result.getRecordMetadata().offset() + "]");
            } else {
                System.out.println("Unable to send message=[" + message + "] due to: " + ex.getMessage());
            }
        });
    }

    @Test
    void sendMessageTest3() {
        String topicName = "hwlTest";
        greetingDTOKafkaTemplate.send(topicName, new GreetingDTO("hello", "world"));
    }

    /**
     * 多对象生产者
     */
    @Test
    void sendMessageTest4() {
        String topicName = "multiObject";
        multiObjectKafkaTemplate.send(topicName, new GreetingDTO("Greeting", "hwl"));
        multiObjectKafkaTemplate.send(topicName, new Farewell("Farewell", 25));
        multiObjectKafkaTemplate.send(topicName, "Simple string message");
    }
}