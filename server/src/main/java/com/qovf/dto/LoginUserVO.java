package com.qovf.dto;

import java.util.List;

/**
 * 登录成功返回体，字段与前端 admin 的 UserResult.data 契约一致。
 */
public record LoginUserVO(
        String avatar,
        String username,
        String nickname,
        List<String> roles,
        List<String> permissions,
        String accessToken,
        String refreshToken,
        String expires
) {
}
