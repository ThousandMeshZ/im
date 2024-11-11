package com.tmesh.im.common.exception;

import com.tmesh.im.common.enums.ResultCodeEnum;
import lombok.Getter;
import org.apache.shiro.authc.AuthenticationException;

/**
 *
 * @author TMesh
 * @version 1.0.0
 * @description 登录异常
 */

public class LoginException extends AuthenticationException {

    @Getter
    private ResultCodeEnum code;

    public LoginException(ResultCodeEnum resultCode) {
        super(resultCode.getInfo());
        this.code = resultCode;
    }

}
