package com.qovf.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 工具：基于 HS256 签发/校验 accessToken 与 refreshToken。
 * 密钥与有效期由 application.yml 的 jwt.* 注入（生产用环境变量覆盖）。
 */
@Component
public class JwtUtil {

    public static final String TYPE_ACCESS = "access";
    public static final String TYPE_REFRESH = "refresh";

    private final SecretKey key;
    private final long accessExpireMs;
    private final long refreshExpireMs;

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-expire-minutes}") long accessMinutes,
            @Value("${jwt.refresh-expire-days}") long refreshDays) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessExpireMs = accessMinutes * 60 * 1000;
        this.refreshExpireMs = refreshDays * 24 * 60 * 60 * 1000;
    }

    public String generateAccessToken(String username, String role) {
        return build(username, role, TYPE_ACCESS, accessExpireMs);
    }

    public String generateRefreshToken(String username, String role) {
        return build(username, role, TYPE_REFRESH, refreshExpireMs);
    }

    private String build(String username, String role, String type, long ttlMs) {
        Date now = new Date();
        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .claim("type", type)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + ttlMs))
                .signWith(key)
                .compact();
    }

    /** 解析并校验签名/有效期，失败抛异常 */
    public Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /** accessToken 过期时刻 */
    public Date accessExpireAt() {
        return new Date(System.currentTimeMillis() + accessExpireMs);
    }
}
