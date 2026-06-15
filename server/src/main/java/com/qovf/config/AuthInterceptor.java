package com.qovf.config;

import com.qovf.common.BusinessException;
import com.qovf.common.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 鉴权拦截器：校验 Authorization: Bearer <accessToken>，通过后把用户名/角色写入请求属性。
 * 白名单（登录/刷新）在 {@link WebConfig} 中排除。
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    public static final String ATTR_USERNAME = "username";
    public static final String ATTR_ROLE = "role";

    private static final int UNAUTHORIZED = 401;

    private final JwtUtil jwtUtil;

    public AuthInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new BusinessException(UNAUTHORIZED, "未登录或登录已过期");
        }
        try {
            Claims claims = jwtUtil.parse(header.substring(7));
            request.setAttribute(ATTR_USERNAME, claims.getSubject());
            request.setAttribute(ATTR_ROLE, claims.get("role", String.class));
            return true;
        } catch (Exception e) {
            throw new BusinessException(UNAUTHORIZED, "登录状态无效，请重新登录");
        }
    }
}
