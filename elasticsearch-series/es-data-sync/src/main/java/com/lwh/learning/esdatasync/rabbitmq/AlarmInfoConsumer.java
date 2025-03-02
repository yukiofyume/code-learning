package com.lwh.learning.esdatasync.rabbitmq;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lwh.learning.esdatasync.elasticsearch.ElasticSearchUtils;
import com.lwh.learning.esdatasync.entity.AlarmInfo;
import net.minidev.json.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author lwh
 * @date 2025-02-26 20:56:59
 * @describe -
 */
@Service
public class AlarmInfoConsumer {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @RabbitListener(queues = "alarm.queue")
    public void handleAlarmMessage(AlarmInfo alarmInfo) {
        try {
            // 1. 保存到 Elasticsearch
            int year = alarmInfo.getAlarmTime().getYear();
            // 2.这里模拟异常到死信息队列
            if (year > 2024) {
                throw new RuntimeException("当前year > 2024");
            }
            ObjectMapper objectMapper = new ObjectMapper();
            String esIndex = "alarm_info" + "_" + year;
            LOGGER.info("接受到消息，推送到指定的 es 索引中, {}", esIndex);
//            ElasticSearchUtils.insertData(esIndex, objectMapper.writeValueAsString(alarmInfo));
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
    }
}
