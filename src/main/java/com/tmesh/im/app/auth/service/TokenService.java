package com.tmesh.im.app.auth.service;

import com.tmesh.im.common.shiro.LoginUser;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : token 服务层
 */
public interface TokenService {

    /**
     * 生成token
     */
    String generateToken();

    /**
     * 通过token查询
     */
    LoginUser queryByToken(String token);

    /**
     * 删除token
     */
    void deleteToken(String token);
}
