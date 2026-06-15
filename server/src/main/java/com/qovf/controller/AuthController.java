package com.qovf.controller;

import com.qovf.common.Result;
import com.qovf.dto.LoginDTO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 认证接口（底座演示实现）。
 * 返回结构与前端 admin 的 UserResult / RefreshTokenResult 契约保持一致。
 * 真实项目中请替换为基于数据库 + JWT/Spring Security 的实现。
 */
@RestController
public class AuthController {

    /** 登录 */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginDTO dto) {
        boolean isAdmin = "admin".equals(dto.getUsername());

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("avatar", "https://avatars.githubusercontent.com/u/44761321");
        data.put("username", dto.getUsername());
        data.put("nickname", isAdmin ? "管理员" : "普通用户");
        data.put("roles", isAdmin ? List.of("admin") : List.of("common"));
        data.put("permissions", isAdmin ? List.of("*:*:*") : List.of("permission:btn:add"));
        data.put("accessToken", "demo-access-token-" + dto.getUsername());
        data.put("refreshToken", "demo-refresh-token-" + dto.getUsername());
        data.put("expires", "2030/10/30 00:00:00");

        return Result.ok(data);
    }

    /** 刷新 token */
    @PostMapping("/refresh-token")
    public Result<Map<String, Object>> refreshToken(@RequestBody Map<String, String> body) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("accessToken", "demo-access-token-refreshed");
        data.put("refreshToken", body.get("refreshToken"));
        data.put("expires", "2030/10/30 00:00:00");
        return Result.ok(data);
    }
}
