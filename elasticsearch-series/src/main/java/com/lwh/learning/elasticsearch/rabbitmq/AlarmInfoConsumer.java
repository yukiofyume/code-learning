package com.lwh.learning.elasticsearch.rabbitmq;

import com.lwh.learning.elasticsearch.entity.AlarmInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author lwh
 * @date 2025-01-14 21:18:36
 * @describe -
 */
@Service
public class AlarmInfoConsumer {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @RabbitListener(queues = "alarm.queue")
    public void handleAlarmMessage(AlarmInfo alarmInfo) {
        try {
            // 1. 保存到 Elasticsearch
            int year = alarmInfo.getAlarmTime().getYear();
            if (year > 2024) {
                throw new RuntimeException("当前year > 2024");
            }
            String esIndex = "alarm_info" + "_" + year;
            LOGGER.info("接受到消息，推送到指定的 es 索引中, {}", esIndex);
            elasticsearchTemplate.save(alarmInfo, IndexCoordinates.of(esIndex));
        } catch (Exception e) {
            // 拒绝消息，不重回队列
            throw new AmqpRejectAndDontRequeueException("message processing error", e);
        }
    }

    @RabbitListener(queues = "dead.letter.queue")
    public void processDeadMessage(AlarmInfo alarmInfo) {
        LOGGER.info("死信队列消费消息");
        // 这里仅仅是为了es的批量消费
        List<AlarmInfo> alarmInfoList = List.of(alarmInfo);
        int year = alarmInfo.getAlarmTime().getYear();
        String esIndex = "alarm_info" + "_" + year;
        elasticsearchTemplate.save(alarmInfoList, IndexCoordinates.of(esIndex));
    }
}