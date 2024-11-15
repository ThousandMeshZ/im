package com.tmesh.im.common.shiro;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 登录用户身份权限
 */
@Data
@Accessors(chain = true) // 链式调用
public class LoginUser {

    /**
     * 用户唯一标识
     */
    private String token;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户手机号
     */
    private String phone;

    /**
     * 登录IP地址
     */
    private String ipAddr;

}

