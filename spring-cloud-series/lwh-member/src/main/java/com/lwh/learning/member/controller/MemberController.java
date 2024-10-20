package com.lwh.learning.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lwh
 * @date 2024-10-19 20:49:49
 * @describe -
 */
@Tag(name = "会员服务接口")
@RestController
public class MemberController {

    @Operation(summary = "会员服务测试")
    @GetMapping("/memberTest")
    public String memberTest() {
        return "这是会员服务测试";
    }
}
