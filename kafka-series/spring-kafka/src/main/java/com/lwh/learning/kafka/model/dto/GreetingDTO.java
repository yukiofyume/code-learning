package com.lwh.learning.kafka.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author lwh
 * @date 2024-08-15 21:04:52
 * @describe -
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GreetingDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1267788067911096469L;

    private String msg;

    private String name;
}
