package com.lwh.learning.elasticsearch.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author lwh
 * @date 2025-01-18 15:21:03
 * @describe -
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlarmInfoReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 7973242585210377239L;

    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",
            locale = "zh_CN",
            timezone = "GMT+8",
            shape = JsonFormat.Shape.STRING)
    private LocalDateTime startAlarmTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",
            locale = "zh_CN",
            timezone = "GMT+8",
            shape = JsonFormat.Shape.STRING)
    private LocalDateTime endAlarmTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",
            locale = "zh_CN",
            timezone = "GMT+8",
            shape = JsonFormat.Shape.STRING)
    private LocalDateTime startHandleTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",
            locale = "zh_CN",
            timezone = "GMT+8",
            shape = JsonFormat.Shape.STRING)
    private LocalDateTime endHandleTime;

    /**
     * 1-已处理，2-未处理
     */
    private Integer handleStatus;

    private String message;

}
