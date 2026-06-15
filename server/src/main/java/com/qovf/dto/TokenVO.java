package com.qovf.dto;

/**
 * 刷新 token 返回体，字段与前端 admin 的 RefreshTokenResult.data 契约一致。
 */
public record TokenVO(
        String accessToken,
        String refreshToken,
        String expires
) {
}
