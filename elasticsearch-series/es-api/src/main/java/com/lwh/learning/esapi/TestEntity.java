package com.lwh.learning.esapi;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author lwh
 * @date 2025-01-18 23:03:06
 * @describe -
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = -959454733296419259L;

    private String id;

    private String title;

    private String content;

    private Long timestamp;
}
