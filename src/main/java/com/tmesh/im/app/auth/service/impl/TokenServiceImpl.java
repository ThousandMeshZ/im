package com.tmesh.im.app.auth.service.impl;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSONUtil;
import com.tmesh.im.app.auth.service.TokenService;
import com.tmesh.im.common.config.PlatformConfig;
import com.tmesh.im.common.constant.HeadConstant;
import com.tmesh.im.common.redis.RedisUtils;
import com.tmesh.im.common.shiro.LoginUser;
import com.tmesh.im.common.shiro.ShiroUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : token 服务层实现
 */
@Service("tokenService")
public class TokenServiceImpl implements TokenService {
    
    @Autowired
    private RedisUtils redisUtils;
    
    @Override
    public String generateToken() {
        LoginUser loginUser = ShiroUtils.getLoginUser();
        String token = loginUser.getToken();
        // 存储到 redis
        this.redisUtils.set(this.makeToken(token), JSONUtil.toJsonStr(loginUser), PlatformConfig.TIMEOUT, TimeUnit.MINUTES);
        return token;
    }
    
    @Override
    public LoginUser queryByToken(String token) {
        String key = this.makeToken(token);
        if (!this.redisUtils.hasKey(key)) {
            return null;
        }
        // 续期
        this.redisUtils.expire(key, PlatformConfig.TIMEOUT, TimeUnit.MINUTES);
        // 转换
        return JSONUtil.toBean(this.redisUtils.get(key), new TypeReference<LoginUser>(){}, false);
    }
    
    @Override
    public void deleteToken(String token) {
        if (!StringUtils.hasText(token)) {
            return ;
        }
        this.redisUtils.delete(this.makeToken(token));
    }
    
    private String makeToken(String token) {
        return HeadConstant.TOKEN_REDIS_APP + token;
    }
}
