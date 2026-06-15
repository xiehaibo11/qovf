package com.qovf.dto;

/**
 * 个人信息返回体，字段与前端 admin 的 UserInfo 契约一致。
 */
public record UserInfoVO(
        String avatar,
        String username,
        String nickname,
        String email,
        String phone,
        String description
) {
}
