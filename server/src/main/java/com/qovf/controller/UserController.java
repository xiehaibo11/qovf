package com.qovf.controller;

import com.qovf.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 个人中心接口（底座演示实现），对应 admin 的 account-settings 页面。
 */
@RestController
public class UserController {

    /** 个人信息 */
    @GetMapping("/mine")
    public Result<Map<String, Object>> mine() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("avatar", "https://avatars.githubusercontent.com/u/44761321");
        data.put("username", "admin");
        data.put("nickname", "管理员");
        data.put("email", "admin@qovf.com");
        data.put("phone", "13800000000");
        data.put("description", "QOVF 管理后台底座演示账号");
        return Result.ok(data);
    }

    /** 个人安全日志（分页表格） */
    @GetMapping("/mine-logs")
    public Result<Map<String, Object>> mineLogs() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("list", List.of());
        data.put("total", 0);
        data.put("pageSize", 10);
        data.put("currentPage", 1);
        return Result.ok(data);
    }
}
