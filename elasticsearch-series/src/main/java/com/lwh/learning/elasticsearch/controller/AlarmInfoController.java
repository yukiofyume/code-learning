package com.lwh.learning.elasticsearch.controller;

import com.lwh.learning.elasticsearch.entity.AlarmInfo;
import com.lwh.learning.elasticsearch.req.AlarmInfoReq;
import com.lwh.learning.elasticsearch.service.AlarmInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author lwh
 * @date 2025-01-14 21:31:11
 * @describe -
 */
@RestController
@RequestMapping("/alarm-info")
public class AlarmInfoController {

    @Autowired
    private AlarmInfoService alarmInfoService;


    @PostMapping("/insert-alarm-info")
    public String insertAlarmInfo(@RequestBody AlarmInfo alarmInfo) {
        alarmInfo.setCreateTime(LocalDateTime.now());
        alarmInfo.setUpdateTime(LocalDateTime.now());
        alarmInfoService.insertAlarmInfo(alarmInfo);
        return "success";
    }

    @PostMapping("/batchInsertAlarmInfo")
    public String batchInsertAlarmInfo(@RequestBody AlarmInfo alarmInfo) {
        LocalDateTime startAlarmTime = LocalDateTime.of(LocalDate.of(2019, 1, 2), LocalTime.MIN);
        for (int i = 0; i < 100; i++) {
            LocalDateTime currentTime = startAlarmTime.plusDays(i);
            List<AlarmInfo> alarmInfos = new ArrayList<>();
            for (int j = 0; j < 1000000; j++) {
                AlarmInfo info = new AlarmInfo();
                int i1 = new Random().nextInt(1440);
                info.setAlarmTime(currentTime.plusMinutes(i1));
                info.setHandleTime(null);
                info.setHandleStatus(1);
                info.setMessage("这一条消息" + info.getAlarmTime().toString());
                info.setCreateTime(LocalDateTime.now());
                info.setUpdateTime(LocalDateTime.now());
                alarmInfos.add(info);
            }
            alarmInfoService.batchInsertAlarmInfo(alarmInfos);
        }

        return "success";
    }

    @PostMapping("/searAlarms")
    public void searAlarms(@RequestBody AlarmInfoReq req) throws IOException {
        alarmInfoService.searAlarms(req);
    }
}
