package com.qovf.controller;

import com.qovf.common.Result;
import com.qovf.config.AuthInterceptor;
import com.qovf.dto.UserInfoVO;
import com.qovf.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 个人中心接口，对应 admin 的 account-settings 页面。需登录（经 AuthInterceptor 鉴权）。
 */
@RestController
public class UserController {

    private final AuthService authService;

    public UserController(AuthService authService) {
        this.authService = authService;
    }

    /** 个人信息（取当前登录用户） */
    @GetMapping("/mine")
    public Result<UserInfoVO> mine(HttpServletRequest request) {
        String username = (String) request.getAttribute(AuthInterceptor.ATTR_USERNAME);
        return Result.ok(authService.currentUserInfo(username));
    }

    /** 个人安全日志（暂无日志表，返回真实空分页；后续接入 sys_login_log） */
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
