package com.lwh.learning.elasticsearch.mapper;

import com.lwh.learning.elasticsearch.entity.AlarmInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author lwh
 * @date 2025-01-14 21:01:10
 * @describe -
 */
@Mapper
public interface AlarmInfoMapper {

    int insertSelective(AlarmInfo alarmInfo);


}