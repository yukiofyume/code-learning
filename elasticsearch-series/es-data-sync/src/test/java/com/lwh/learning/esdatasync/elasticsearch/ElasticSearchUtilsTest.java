package com.lwh.learning.esdatasync.elasticsearch;

import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import com.lwh.learning.common.util.JacksonUtils;
import com.lwh.learning.esdatasync.entity.AlarmInfo;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author lwh
 * @date 2025-03-01 15:01:43
 * @describe -
 */

class ElasticSearchUtilsTest {

    /**
     * 创建索引
     */
    @Test
    void createIndexTest() {
        CreateIndexResponse index = ElasticSearchUtils.createIndex("alarm_info_2025", "alarm_info", "3", "2");
        System.out.println(index.acknowledged());
    }

    @Test
    void insertTest() {
        AlarmInfo alarmInfo = new AlarmInfo();
        alarmInfo.setId(1L);
        alarmInfo.setAlarmTime(LocalDateTime.now());
        alarmInfo.setHandleTime(LocalDateTime.now());
        alarmInfo.setHandleStatus(1);
        alarmInfo.setMessage("这是一条测试数据1");
        alarmInfo.setCreateTime(LocalDateTime.now());
        alarmInfo.setUpdateTime(LocalDateTime.now());
        IndexResponse response = ElasticSearchUtils.insertData("alarm_info_2025", String.valueOf(alarmInfo.getId()), alarmInfo);
        System.out.println(response.version());
    }
}