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
            if (alarmInfo.getId() == 1) {
                throw new Exception("模拟异常");
            }
            String esIndex = "alarm_info_new";
            LOGGER.info("接受到消息，推送到指定的 es 索引中, {}", esIndex);
            ElasticSearchUtils.insertData(esIndex, alarmInfo.getId().toString(), alarmInfo);
        } catch (Exception e) {
            // 拒绝消息，不重回队列
            throw new AmqpRejectAndDontRequeueException("message processing error", e);
        }
    }

    @RabbitListener(queues = "dead.letter.queue")
    public void processDeadMessage(AlarmInfo alarmInfo) {
        LOGGER.info("死信队列消费消息");
        String esIndex = "alarm_info_new";
        ElasticSearchUtils.insertData(esIndex, alarmInfo.getId().toString(), alarmInfo);
    }
}
