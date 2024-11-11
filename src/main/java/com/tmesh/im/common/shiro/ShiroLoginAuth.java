package com.tmesh.im.common.shiro;

import lombok.Data;
import org.apache.shiro.authc.AuthenticationToken;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : Shiro token
 */
@Data
public class ShiroLoginAuth implements AuthenticationToken {

    private String phone;
    private char[] password;

    public ShiroLoginAuth(String phone, String password) {
        this.phone = phone;
        this.password = password.toCharArray();
    }

    @Override
    public Object getPrincipal() {
        return phone;
    }

    @Override
    public Object getCredentials() {
        return password;
    }

}
