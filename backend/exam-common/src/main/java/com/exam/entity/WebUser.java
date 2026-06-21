package com.exam.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("web_user")
public class WebUser {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String password;
    private String nickname;
    private String email;
    /** 用户分类：TEACHER-老师，STUDENT-学生 */
    private String userType;
    /** 学制偏好：63-六三制，54-五四制 */
    private String educationSystem;
    private Integer status;

    // ===== VIP 预留字段 =====
    /** VIP等级：0-普通用户，1-VIP1，2-VIP2 ... */
    private Integer vipLevel;
    /** VIP过期时间，NULL表示永久或非VIP */
    private LocalDateTime vipExpireTime;
    // =========================

    // ===== 微信/App 预留字段 =====
    /** 微信开放平台 openid（网站应用扫码登录） */
    private String wechatOpenid;
    /** 微信 unionid（跨平台统一标识，公众号+小程序+网站应用） */
    private String wechatUnionid;
    /** 微信小程序 openid */
    private String wechatMpOpenid;
    /** 头像URL */
    private String avatarUrl;
    /** 手机号 */
    private String phone;
    // ===============================

    @TableLogic
    private Integer deleted;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
