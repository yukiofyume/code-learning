package com.lwh.learning.esdatasync.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author lwh
 * @date 2025-02-26 20:57:42
 * @describe -
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlarmInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 2867690892361357884L;

//    @TableId(type = IdType.AUTO)
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