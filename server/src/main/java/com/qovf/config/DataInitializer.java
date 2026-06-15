package com.qovf.config;

import com.qovf.entity.SysUser;
import com.qovf.service.SysUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 初始化系统内置账号（首次启动且不存在时创建）。
 * 说明：这是系统**初始管理账号**的引导，非业务模拟数据；密码用 BCrypt 加密存库。
 * 生产首次部署后应立即修改默认密码。
 */
@Component
public class DataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final SysUserService userService;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(SysUserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        seed("admin", "admin", "管理员", "admin123", "admin@qovf.com");
        seed("common", "common", "普通用户", "common123", "common@qovf.com");
    }

    private void seed(String username, String role, String nickname, String rawPwd, String email) {
        if (userService.getByUsername(username) != null) {
            return;
        }
        SysUser u = new SysUser();
        u.setUsername(username);
        u.setPassword(passwordEncoder.encode(rawPwd));
        u.setNickname(nickname);
        u.setRole(role);
        u.setEmail(email);
        u.setAvatar("https://avatars.githubusercontent.com/u/44761321");
        u.setDescription("QOVF 初始" + nickname + "账号，请尽快修改默认密码");
        u.setStatus(1);
        userService.save(u);
        log.info("已初始化账号: {} (角色 {})", username, role);
    }
}
