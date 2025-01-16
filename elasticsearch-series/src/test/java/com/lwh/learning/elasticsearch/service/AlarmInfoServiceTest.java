package com.lwh.learning.elasticsearch.service;

import com.lwh.learning.elasticsearch.entity.AlarmInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

/**
 * @author lwh
 * @date 2025-01-14 21:25:02
 * @describe -
 */
@SpringBootTest
class AlarmInfoServiceTest {

    @Autowired
    private AlarmInfoService alarmInfoService;

    @Test
    void insertAlarmInfoTest() {
        AlarmInfo alarmInfo = new AlarmInfo();
        alarmInfo.setAlarmTime(LocalDateTime.now());
        alarmInfo.setHandleTime(null);
        alarmInfo.setHandleStatus(1);
        alarmInfo.setMessage("这是条测试数据");
        alarmInfo.setCreateTime(LocalDateTime.now());
        alarmInfo.setUpdateTime(LocalDateTime.now());

        alarmInfoService.insertAlarmInfo(alarmInfo);
    }
}