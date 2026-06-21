package com.exam.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WeChatLoginDTO {
    /** 微信 OAuth 回调返回的 authorization code */
    @NotBlank(message = "code不能为空")
    private String code;

    /** OAuth state 参数，用于防CSRF */
    private String state;
}
