package com.tmesh.im.app.auth.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 用户信息视图4
 */
@Data
public class AuthVo04 {

    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    private String phone;

    /**
     * 验证码
     */
    @NotBlank(message = "验证码不能为空")
    private String code;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;

}
