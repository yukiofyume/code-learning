package com.lwh.learning.elasticsearch.service;

import com.lwh.learning.elasticsearch.entity.AlarmInfo;
import com.lwh.learning.elasticsearch.mapper.AlarmInfoMapper;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author lwh
 * @date 2025-01-14 21:12:26
 * @describe -
 */
@Service
public class AlarmInfoService {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AlarmInfoMapper alarmInfoMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Transactional(rollbackFor = Exception.class)
    public void insertAlarmInfo(AlarmInfo alarmInfo) {
        // 1. 插入 PostgreSQL
        alarmInfoMapper.insertSelective(alarmInfo);

        // 2. 异步发送到 RabbitMQ
        rabbitTemplate.convertAndSend("alarm.exchange", "alarm.routing.key", alarmInfo);
    }

    /**
     * 批量将数据同步到es中
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchInsertAlarmInfo(List<AlarmInfo> alarmInfos) {
        LOGGER.info("need send to  size:{}", alarmInfos.size());
        try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
            AlarmInfoMapper mapper = sqlSession.getMapper(AlarmInfoMapper.class);
            for (AlarmInfo alarmInfo : alarmInfos) {
                mapper.insertSelective(alarmInfo);
            }
            sqlSession.commit();
        }
//        LOGGER.info("need send to es size:{}", alarmInfos.size());
//        int year = alarmInfos.get(0).getAlarmTime().getYear();
//        elasticsearchTemplate.save(alarmInfos, IndexCoordinates.of("alarm_info" + "_" + year));
    }
}
