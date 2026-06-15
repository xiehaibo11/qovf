package com.qovf.controller;

import com.qovf.common.Result;
import com.qovf.dto.LoginDTO;
import com.qovf.dto.LoginUserVO;
import com.qovf.dto.TokenVO;
import com.qovf.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 认证接口：基于数据库账号 + BCrypt + JWT。
 */
@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /** 登录 */
    @PostMapping("/login")
    public Result<LoginUserVO> login(@Valid @RequestBody LoginDTO dto) {
        return Result.ok(authService.login(dto));
    }

    /** 刷新 token */
    @PostMapping("/refresh-token")
    public Result<TokenVO> refreshToken(@RequestBody Map<String, String> body) {
        return Result.ok(authService.refresh(body.get("refreshToken")));
    }
}
