package com.exam.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.exam.common.Result;
import com.exam.dto.LoginDTO;
import com.exam.dto.RegisterDTO;
import com.exam.entity.WebUser;
import com.exam.mapper.WebUserMapper;
import com.exam.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 前台用户认证服务
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final WebUserMapper webUserMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public Result<?> login(LoginDTO loginDTO) {
        WebUser user = webUserMapper.selectOne(
            new LambdaQueryWrapper<WebUser>().eq(WebUser::getUsername, loginDTO.getUsername())
        );

        if (user == null || !passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            return Result.error(401, "用户名或密码错误");
        }

        if (user.getStatus() != 1) {
            return Result.error(403, "账号已被禁用");
        }

        String token = jwtUtil.generateToken(user.getUsername(), "WEB");
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        result.put("nickname", user.getNickname());
        result.put("userType", user.getUserType());
        result.put("userSource", "WEB");
        result.put("educationSystem", user.getEducationSystem() != null ? user.getEducationSystem() : "63");
        return Result.success(result);
    }

    public Result<?> register(RegisterDTO registerDTO) {
        WebUser existing = webUserMapper.selectOne(
            new LambdaQueryWrapper<WebUser>().eq(WebUser::getUsername, registerDTO.getUsername())
        );
        if (existing != null) {
            return Result.error(400, "用户名已存在");
        }

        WebUser user = new WebUser();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setNickname(registerDTO.getNickname() != null ? registerDTO.getNickname() : registerDTO.getUsername());
        user.setEmail(registerDTO.getEmail());
        user.setUserType("STUDENT");
        user.setStatus(1);
        user.setDeleted(0);
        webUserMapper.insert(user);

        return Result.success("注册成功");
    }
}
