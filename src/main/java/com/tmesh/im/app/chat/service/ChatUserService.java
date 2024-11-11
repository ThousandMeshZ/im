package com.tmesh.im.app.chat.service;

import cn.hutool.core.lang.Dict;
import com.tmesh.im.app.auth.vo.AuthVo01;
import com.tmesh.im.app.chat.domain.ChatUser;
import com.tmesh.im.app.chat.vo.MyVo09;
import com.tmesh.im.common.web.service.BaseService;
import org.apache.shiro.authc.AuthenticationToken;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 用户表 服务层
 */
public interface ChatUserService extends BaseService<ChatUser> {

    /**
     * 通过手机+密码注册
     */
    void register(AuthVo01 authVo);

    /**
     * 通过手机号查询
     */
    ChatUser queryByPhone(String phone);

    /**
     * 重置密码
     */
    Integer resetPass(Long userId, String password);

    /**
     * 修改密码
     */
    Integer editPass(String password, String pwd);

    /**
     * 修改微聊号
     */
    void editChatNo(String chatNo);

    /**
     * 获取基本信息
     */
    MyVo09 getInfo();

    /**
     * 获取二维码
     */
    String getQrCode();

    /**
     * 用户注销
     */
    void deleted();

    /**
     * 执行登录/返回token
     */
    Dict doLogin(AuthenticationToken authenticationToken);

    /**
     * 退出登录
     */
    void logout();

    /**
     * 刷新
     */
    void refresh();

}

