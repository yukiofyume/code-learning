package com.lwh.learning.order.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lwh
 * @date 2024-10-20 12:53:44
 * @describe -
 */
@Tag(name = "订单服务测试")
@RestController
public class TestController {

    @Operation(summary = "订单服务测试接口")
    @GetMapping("/orderTest")
    public String orderTest() {
        return "这是订单服务测试";
    }
}
