package com.exam.web.controller;

import com.exam.common.Result;
import com.exam.dto.LoginDTO;
import com.exam.dto.RegisterDTO;
import com.exam.dto.WeChatLoginDTO;
import com.exam.service.AuthService;
import com.exam.service.WeChatAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final WeChatAuthService weChatAuthService;

    @PostMapping("/login")
    public Result<?> login(@Valid @RequestBody LoginDTO loginDTO) {
        return authService.login(loginDTO);
    }

    @PostMapping("/register")
    public Result<?> register(@Valid @RequestBody RegisterDTO registerDTO) {
        return authService.register(registerDTO);
    }

    @PostMapping("/wechat/login")
    public Result<?> wechatLogin(@Valid @RequestBody WeChatLoginDTO dto) {
        return weChatAuthService.login(dto);
    }

    @GetMapping("/wechat/url")
    public Result<?> wechatAuthUrl() {
        return weChatAuthService.getAuthUrl();
    }
}
