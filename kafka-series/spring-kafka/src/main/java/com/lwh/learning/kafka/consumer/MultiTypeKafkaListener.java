package com.lwh.learning.kafka.consumer;

import com.lwh.learning.kafka.model.dto.Farewell;
import com.lwh.learning.kafka.model.dto.GreetingDTO;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @author lwh
 * @date 2024-08-15 21:37:19
 * @describe 多对象消费者
 */
@Component
@KafkaListener(groupId = "multiObjectGroup", topics = "multiObject")
public class MultiTypeKafkaListener {

    @KafkaHandler
    public void handlerGreeting(GreetingDTO greetingDTO) {
        System.out.println("Greeting received: " + greetingDTO.toString());
    }

    @KafkaHandler
    public void handlerFarewell(Farewell farewell) {
        System.out.println("Farewell received: " + farewell.toString());
    }

    /**
     * 这个必须配置，不然会有异常消息来的时候，所有都会消息都无法消费
     *
     * @param object o
     */
    @KafkaHandler(isDefault = true)
    public void unknown(Object object) {
        System.out.println("Unknown type received: " + object.toString());
    }
}
