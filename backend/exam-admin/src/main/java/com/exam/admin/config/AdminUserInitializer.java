package com.exam.admin.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.exam.entity.User;
import com.exam.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

/**
 * 管理员用户初始化器
 * 启动时检测是否存在admin用户，不存在则自动创建并在控制台输出密码
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminUserInitializer implements ApplicationRunner {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    private static final String ADMIN_USERNAME = "admin";
    private static final String DEFAULT_NICKNAME = "管理员";
    private static final int GENERATED_PASSWORD_LENGTH = 12;
    private static final String PASSWORD_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    @Override
    public void run(ApplicationArguments args) {
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getUsername, ADMIN_USERNAME)
        );

        if (count > 0) {
            log.info("管理员用户已存在，跳过初始化");
            return;
        }

        String rawPassword = generatePassword();
        User admin = new User();
        admin.setUsername(ADMIN_USERNAME);
        admin.setPassword(passwordEncoder.encode(rawPassword));
        admin.setNickname(DEFAULT_NICKNAME);
        admin.setStatus(1);
        admin.setDeleted(0);
        userMapper.insert(admin);

        log.info("========================================");
        log.info("管理员用户自动创建成功！");
        log.info("用户名: {}", ADMIN_USERNAME);
        log.info("密码: {}", rawPassword);
        log.info("请妥善保管密码，首次登录后建议修改密码");
        log.info("========================================");
    }

    private String generatePassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(GENERATED_PASSWORD_LENGTH);
        for (int i = 0; i < GENERATED_PASSWORD_LENGTH; i++) {
            sb.append(PASSWORD_CHARS.charAt(random.nextInt(PASSWORD_CHARS.length())));
        }
        return sb.toString();
    }
}
