package com.lwh.learning.kafka.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author lwh
 * @date 2024-08-15 21:19:06
 * @describe -
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Farewell implements Serializable {
    @Serial
    private static final long serialVersionUID = -5551378899887731173L;

    private String message;

    private Integer remainingMinutes;
}
