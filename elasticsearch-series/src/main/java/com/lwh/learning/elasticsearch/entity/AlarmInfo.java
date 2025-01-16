package com.lwh.learning.elasticsearch.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author lwh
 * @date 2025-01-14 21:01:10
 * @describe -
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlarmInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 2867690892361357884L;
    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",
            locale = "zh_CN",
            timezone = "GMT+8",
            shape = JsonFormat.Shape.STRING)
    private LocalDateTime alarmTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",
            locale = "zh_CN",
            timezone = "GMT+8",
            shape = JsonFormat.Shape.STRING)
    private LocalDateTime handleTime;

    /**
     * 1-已处理，2-未处理
     */
    private Integer handleStatus;

    private String message;

    /**
     * 1-已同步到es, 2-未同步到es
     */
    private Integer esSyncStatus;

    /**
     * 同步到es的次数
     */
    private Integer retryEsCount;

    /**
     * 最后一次同步到 es 的时间
     */
    private LocalDateTime lastRetryEsTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",
            locale = "zh_CN",
            timezone = "GMT+8",
            shape = JsonFormat.Shape.STRING)
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",
            locale = "zh_CN",
            timezone = "GMT+8",
            shape = JsonFormat.Shape.STRING)
    private LocalDateTime updateTime;
}