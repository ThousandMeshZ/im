package com.tmesh.im.app.auth.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 用户信息视图1
 */
@Data
public class AuthVo01 {

    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    private String phone;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 昵称
     */
    @NotBlank(message = "昵称不能为空")
    @Size(max = 20, message = "昵称长度不能大于20")
    private String nickName;

    /**
     * 验证码
     */
    @NotBlank(message = "验证码不能为空")
    private String code;

}