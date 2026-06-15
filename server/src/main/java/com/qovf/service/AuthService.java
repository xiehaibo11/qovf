package com.qovf.service;

import com.qovf.common.BusinessException;
import com.qovf.common.JwtUtil;
import com.qovf.dto.LoginDTO;
import com.qovf.dto.LoginUserVO;
import com.qovf.dto.TokenVO;
import com.qovf.dto.UserInfoVO;
import com.qovf.entity.SysUser;
import io.jsonwebtoken.Claims;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * 认证业务：登录、刷新 token、当前用户信息。数据来自真实库（sys_user）。
 */
@Service
public class AuthService {

    private static final String ROLE_ADMIN = "admin";

    private final SysUserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(SysUserService userService, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public LoginUserVO login(LoginDTO dto) {
        SysUser user = userService.getByUsername(dto.getUsername());
        if (user == null || !passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException("账号已停用，请联系管理员");
        }
        return new LoginUserVO(
                user.getAvatar(),
                user.getUsername(),
                user.getNickname(),
                List.of(user.getRole()),
                permissionsOf(user.getRole()),
                jwtUtil.generateAccessToken(user.getUsername(), user.getRole()),
                jwtUtil.generateRefreshToken(user.getUsername(), user.getRole()),
                expires()
        );
    }

    public TokenVO refresh(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new BusinessException("缺少 refreshToken");
        }
        Claims claims;
        try {
            claims = jwtUtil.parse(refreshToken);
        } catch (Exception e) {
            throw new BusinessException("refreshToken 无效或已过期，请重新登录");
        }
        if (!JwtUtil.TYPE_REFRESH.equals(claims.get("type", String.class))) {
            throw new BusinessException("token 类型错误");
        }
        String username = claims.getSubject();
        String role = claims.get("role", String.class);
        return new TokenVO(
                jwtUtil.generateAccessToken(username, role),
                jwtUtil.generateRefreshToken(username, role),
                expires()
        );
    }

    public UserInfoVO currentUserInfo(String username) {
        SysUser user = userService.getByUsername(username);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return new UserInfoVO(
                user.getAvatar(),
                user.getUsername(),
                user.getNickname(),
                user.getEmail(),
                user.getPhone(),
                user.getDescription()
        );
    }

    /** 角色 → 按钮级权限。后续可改为从权限表加载。 */
    private List<String> permissionsOf(String role) {
        return ROLE_ADMIN.equals(role)
                ? List.of("*:*:*")
                : List.of("permission:btn:add", "permission:btn:edit");
    }

    private String expires() {
        return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(jwtUtil.accessExpireAt());
    }
}
