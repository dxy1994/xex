package com.exam.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.exam.common.Result;
import com.exam.dto.WeChatLoginDTO;
import com.exam.entity.WebUser;
import com.exam.mapper.WebUserMapper;
import com.exam.security.JwtUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 微信 OAuth 登录服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "wechat.appid")
public class WeChatAuthService {

    private final WebUserMapper webUserMapper;
    private final JwtUtil jwtUtil;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${wechat.appid}")
    private String appid;

    @Value("${wechat.secret}")
    private String secret;

    @Value("${wechat.redirect-uri}")
    private String redirectUri;

    private static final String AUTH_URL =
        "https://open.weixin.qq.com/connect/qrconnect?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_login&state=%s#wechat_redirect";
    private static final String ACCESS_TOKEN_URL =
        "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";
    private static final String USERINFO_URL =
        "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s&lang=zh_CN";

    /**
     * 获取微信 OAuth 授权 URL
     */
    public Result<?> getAuthUrl() {
        String state = UUID.randomUUID().toString().substring(0, 16);
        String url = String.format(AUTH_URL, appid, redirectUri, state);
        Map<String, String> result = new HashMap<>();
        result.put("url", url);
        result.put("state", state);
        return Result.success(result);
    }

    public Result<?> login(WeChatLoginDTO dto) {
        try {
            // 1. 用 code 换取 access_token 和 openid
            String tokenUrl = String.format(ACCESS_TOKEN_URL, appid, secret, dto.getCode());
            ResponseEntity<String> tokenResp = restTemplate.getForEntity(tokenUrl, String.class);
            JsonNode tokenJson = objectMapper.readTree(tokenResp.getBody());

            if (tokenJson.has("errcode") && tokenJson.get("errcode").asInt() != 0) {
                log.error("微信获取access_token失败: {}", tokenJson);
                return Result.error(400, "微信授权失败，请重试");
            }

            String accessToken = tokenJson.get("access_token").asText();
            String openid = tokenJson.get("openid").asText();
            String unionid = tokenJson.has("unionid") ? tokenJson.get("unionid").asText() : null;

            // 2. 获取微信用户信息
            String userinfoUrl = String.format(USERINFO_URL, accessToken, openid);
            ResponseEntity<String> userinfoResp = restTemplate.getForEntity(userinfoUrl, String.class);
            JsonNode userinfoJson = objectMapper.readTree(userinfoResp.getBody());
            String nickname = userinfoJson.has("nickname") ? userinfoJson.get("nickname").asText() : "微信用户";
            String avatarUrl = userinfoJson.has("headimgurl") ? userinfoJson.get("headimgurl").asText() : null;

            // 3. 查找或创建用户
            WebUser user = webUserMapper.selectOne(
                new LambdaQueryWrapper<WebUser>().eq(WebUser::getWechatOpenid, openid)
            );

            if (user == null) {
                user = new WebUser();
                user.setUsername("wx_" + UUID.randomUUID().toString().substring(0, 8));
                user.setPassword(""); // 微信用户无密码
                user.setNickname(nickname);
                user.setUserType("STUDENT");
                user.setStatus(1);
                user.setDeleted(0);
                user.setWechatOpenid(openid);
                user.setWechatUnionid(unionid);
                user.setAvatarUrl(avatarUrl);
                webUserMapper.insert(user);
            } else {
                // 更新用户信息
                user.setNickname(nickname);
                user.setAvatarUrl(avatarUrl);
                if (unionid != null) {
                    user.setWechatUnionid(unionid);
                }
                webUserMapper.updateById(user);
            }

            if (user.getStatus() != 1) {
                return Result.error(403, "账号已被禁用");
            }

            // 4. 生成 JWT
            String token = jwtUtil.generateToken(user.getUsername(), "WEB");
            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("userId", user.getId());
            result.put("username", user.getUsername());
            result.put("nickname", user.getNickname());
            result.put("userType", user.getUserType());
            result.put("avatarUrl", user.getAvatarUrl());
            result.put("userSource", "WEB");
            result.put("educationSystem", user.getEducationSystem() != null ? user.getEducationSystem() : "63");
            return Result.success(result);

        } catch (Exception e) {
            log.error("微信登录异常", e);
            return Result.error(500, "微信登录失败，请稍后重试");
        }
    }
}
