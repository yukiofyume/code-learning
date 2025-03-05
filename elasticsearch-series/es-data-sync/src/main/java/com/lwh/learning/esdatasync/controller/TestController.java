package com.lwh.learning.esdatasync.controller;

import com.lwh.learning.esdatasync.elasticsearch.ElasticSearchUtils;
import com.lwh.learning.esdatasync.entity.AlarmInfo;
import com.lwh.learning.esdatasync.mapper.AlarmInfoMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * @author lwh
 * @date 2025-03-03 21:12:07
 * @describe -
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private AlarmInfoMapper alarmInfoMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostMapping("/testInsert")
    public String testInsert(@RequestBody AlarmInfo alarmInfo) {
        AlarmInfo alarmInfo1 = alarmInfoMapper.selectById(alarmInfo.getId());
        if (Objects.isNull(alarmInfo1)) {
            alarmInfoMapper.insertSelective(alarmInfo);
        }
        rabbitTemplate.convertAndSend("alarm.exchange", "alarm.routing.key", alarmInfo);
        return "success";
    }
}
