package com.lwh.learning.kafka;

import com.lwh.learning.kafka.model.dto.GreetingDTO;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

/**
 * @author lwh
 * @date 2024-08-15 20:07:52
 * @describe -
 */
@SpringBootApplication
public class SpringKafkaApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringKafkaApplication.class, args);
    }


    //    @KafkaListener(topics = "hwlTest", groupId = "foo")
//    @KafkaListener(
//            topicPartitions = @TopicPartition(topic = "hwlTest",
//            partitionOffsets = {
//                    @PartitionOffset(partition = "0", initialOffset = "0"),
//                    @PartitionOffset(partition = "3",initialOffset = "0")}),
//            containerFactory = "kafkaListenerContainerFactory"
//    )
    @KafkaListener(topics = "hwlTest",
            containerFactory = "kafkaListenerContainerFactory", groupId = "foo")
    public void listenGroupFoo(@Payload String message, @Header(KafkaHeaders.RECEIVED_PARTITION) int partition) {
        System.out.println("Received message in group foo: " + message + " from partition: " + partition);
    }

    @KafkaListener(topics = "hwlTest", containerFactory = "greetingDTOConcurrentKafkaListenerContainerFactory", groupId = "greeting")
    public void greetingListener(GreetingDTO greetingDTO) {
        System.out.println("Receive message in group greeting: " + greetingDTO.toString());
    }


}
