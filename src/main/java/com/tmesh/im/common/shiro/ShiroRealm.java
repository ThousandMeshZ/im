package com.tmesh.im.common.shiro;

import cn.hutool.core.util.RandomUtil;
import com.tmesh.im.app.chat.domain.ChatUser;
import com.tmesh.im.app.chat.service.ChatUserService;
import com.tmesh.im.app.auth.service.TokenService;
import com.tmesh.im.common.enums.ResultCodeEnum;
import com.tmesh.im.common.exception.LoginException;
import com.tmesh.im.common.utils.IpUtils;
import com.tmesh.im.common.utils.ServletUtils;
import jakarta.annotation.Resource;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.context.annotation.Lazy;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : ShiroRealm
 */
public class ShiroRealm extends AuthorizingRealm {

    @Lazy
    @Resource
    private TokenService tokenService;

    @Lazy
    @Resource
    private ChatUserService chatUserService;

    /**
     * 提供用户信息，返回权限信息
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        Object principal = ShiroUtils.getSubject().getPrincipal();
        if (principal == null) {
            return null;
        }
        // 后台管理
        if (principal instanceof LoginUser) {
            SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
            return simpleAuthorizationInfo;
        }
        return null;
    }

    /**
     * 必须重写此方法，不然会报错
     */
    @Override
    public boolean supports(AuthenticationToken authenticationToken) {
        return authenticationToken instanceof ShiroLoginToken
                || authenticationToken instanceof ShiroLoginAuth
                || authenticationToken instanceof ShiroLoginPhone;
    }

    /**
     * 身份认证/登录，验证用户是不是拥有相应的身份； 用于登陆认证
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        // token
        if (authenticationToken instanceof ShiroLoginToken) {
            String token = (String) authenticationToken.getPrincipal();
            LoginUser loginUser = this.tokenService.queryByToken(token);
            if (loginUser == null) {
                throw new LoginException(ResultCodeEnum.UNAUTHORIZED);
            }
            // 加密盐
            String salt = Md5Utils.salt();
            // 对 token 加密
            String credentials = Md5Utils.credentials(token, salt);
            return new SimpleAuthenticationInfo(loginUser, credentials, ByteSource.Util.bytes(salt), this.getName());
        }
        // 手机+密码登录
        if (authenticationToken instanceof ShiroLoginAuth) {
            ShiroLoginAuth authToken = (ShiroLoginAuth) authenticationToken;
            return this.makeLoginUser(authToken.getPhone(), true);
        }
        // 手机+验证码登录
        if (authenticationToken instanceof  ShiroLoginPhone) {
            ShiroLoginPhone phoneToken = (ShiroLoginPhone) authenticationToken;
            return makeLoginUser(phoneToken.getPhone(), false);
        }
        return null;
    }

    /**
     * 组装登录对象
     */
    private SimpleAuthenticationInfo makeLoginUser(String phone, boolean isPassword) {
        // 查询用户
        ChatUser chatUser = this.chatUserService.queryByPhone(phone);
        // 处理用户
        if (chatUser == null) {
            throw new AuthenticationException("手机号或密码不正确"); // 手机不存在
        }
        // 查询权限
        LoginUser loginUser = new LoginUser()
                .setUserId(chatUser.getUserId())
                .setPhone(chatUser.getPhone())
                .setIpAddr(IpUtils.getIpAddr(ServletUtils.getRequest()))
                .setToken(RandomUtil.randomString(32));
        String pwd = chatUser.getPassword();
        String salt = chatUser.getSalt();
        if (!isPassword) {
            // 加密盐
            salt = Md5Utils.salt();
            // 对 token 加密
            pwd = Md5Utils.credentials(pwd, salt);
        }
        // 登陆
        return new SimpleAuthenticationInfo(loginUser, pwd, ByteSource.Util.bytes(salt), getName());
    }
}
