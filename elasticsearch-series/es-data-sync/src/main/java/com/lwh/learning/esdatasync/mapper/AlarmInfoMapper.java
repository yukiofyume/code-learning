package com.lwh.learning.esdatasync.mapper;

import com.lwh.learning.esdatasync.entity.AlarmInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author lwh
 * @date 2025-02-26 21:01:03
 * @describe -
 */
@Mapper
//public interface AlarmInfoMapper extends BaseMapper<AlarmInfo> {
public interface AlarmInfoMapper {

    int insertSelective(AlarmInfo alarmInfo);

    List<AlarmInfo> selectAll(@Param("lastId") long lastId,
                              @Param("limit") int limit);

}
