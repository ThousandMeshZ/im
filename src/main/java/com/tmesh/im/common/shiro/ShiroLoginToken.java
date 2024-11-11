package com.tmesh.im.common.shiro;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : Shiro token
 */
public class ShiroLoginToken implements AuthenticationToken {
    private static final long serialVersionUID = 1L;

    private String token;

    public ShiroLoginToken(String token) {
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}
