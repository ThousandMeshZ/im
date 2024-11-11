package com.tmesh.im.common.shiro;

import cn.hutool.extra.servlet.JakartaServletUtil;
import com.tmesh.im.common.constant.HeadConstant;
import com.tmesh.im.common.utils.ServletUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description :Shiro工具类
 */
public class ShiroUtils {

    public static Subject getSubject() {
        return SecurityUtils.getSubject();
    }

    public static LoginUser getLoginUser() {
        return (LoginUser) getSubject().getPrincipal();
    }

    /**
     * 是否登录
     */
    public static boolean isLogin() {
        return ShiroUtils.getLoginUser() != null;
    }

    public static String getToken() {
        LoginUser loginUser = ShiroUtils.getLoginUser();
        if (loginUser != null) {
            return loginUser.getToken();
        }
        return ServletUtils.getRequest().getHeader(HeadConstant.TOKEN_HEADER_ADMIN);
    }

    public static String getPhone() {
        return getLoginUser().getPhone();
    }

    public static Long getUserId() {
        return getLoginUser().getUserId();
    }
}

