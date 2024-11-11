package com.tmesh.im.app.auth.controller;

import cn.hutool.core.lang.Dict;
import com.tmesh.im.app.auth.vo.AuthVo01;
import com.tmesh.im.app.auth.vo.AuthVo02;
import com.tmesh.im.app.auth.vo.AuthVo03;
import com.tmesh.im.app.auth.vo.AuthVo04;
import com.tmesh.im.app.chat.domain.ChatUser;
import com.tmesh.im.app.chat.service.ChatUserService;
import com.tmesh.im.app.sms.enums.SmsTypeEnum;
import com.tmesh.im.app.sms.service.SmsService;
import com.tmesh.im.app.sms.vo.SmsVo;
import com.tmesh.im.common.aspectj.IgnoreAuth;
import com.tmesh.im.common.aspectj.SubmitRepeat;
import com.tmesh.im.common.exception.BaseException;
import com.tmesh.im.common.shiro.ShiroLoginAuth;
import com.tmesh.im.common.shiro.ShiroLoginPhone;
import com.tmesh.im.common.version.ApiVersion;
import com.tmesh.im.common.version.VersionEnum;
import com.tmesh.im.common.web.controller.BaseController;
import com.tmesh.im.common.web.domain.AjaxResult;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 认证控制器
 */
@RestController
@Slf4j
@RequestMapping("/auth")
public class AuthController extends BaseController {

    @Resource
    private ChatUserService chatUserService;

    @Resource
    private SmsService smsService;

    /**
     * 发送验证码（登录/注册/忘记密码）
     */
    @IgnoreAuth
    @ApiVersion(VersionEnum.V1_0_0)
    @PostMapping(value = "/sendCode")
    @SubmitRepeat
    public AjaxResult sendCode(@RequestBody @Validated SmsVo smsVo) {
        ChatUser chatUser = this.chatUserService.queryByPhone(smsVo.getPhone());
        switch (smsVo.getType()) {
            case LOGIN-> {}
            case FORGET -> {
                if (chatUser == null) {
                    throw new BaseException("用户未注册，请先注册");
                }
            }
            case REGISTERED -> {
                if (chatUser != null) {
                    throw new BaseException("用户已注册，请直接登录");
                }
            }
            default -> {}
        }
        return AjaxResult.success(smsService.sendSms(smsVo)).put("msg", "验证码已发送");
    }

    /**
     * 注册方法
     */
    @IgnoreAuth
    @ApiVersion(VersionEnum.V1_0_0)
    @PostMapping(value = "/register")
    public AjaxResult register(@Validated @RequestBody AuthVo01 authVo) {
        // 验证
        this.smsService.verifySms(authVo.getPhone(), authVo.getCode(), SmsTypeEnum.REGISTERED);
        // 注册
        this.chatUserService.register(authVo);
        return AjaxResult.success("注册成功，请登录");
    }

    /**
     * 登录方法（根据手机+密码登录）
     */
    @IgnoreAuth
    @ApiVersion(VersionEnum.V1_0_0)
    @PostMapping("/login")
    public AjaxResult login(@Validated @RequestBody AuthVo02 authVo) {
        // 执行登录
        ShiroLoginAuth loginAuth = new ShiroLoginAuth(authVo.getPhone(), authVo.getPassword());
        Dict dict = this.chatUserService.doLogin(loginAuth);
        return AjaxResult.success(dict);
    }

    /**
     * 登录方法（根据手机+验证码登录）
     */
    @IgnoreAuth
    @ApiVersion(VersionEnum.V1_0_0)
    @PostMapping("/loginByCode")
    public AjaxResult loginByCode(@Validated @RequestBody AuthVo03 authVo) {
        // 验证
        // TODO 需实现自己的发送短信验证码方法
        this.smsService.verifySms(authVo.getPhone(), authVo.getCode(), SmsTypeEnum.LOGIN);
        // 执行登陆
        ShiroLoginPhone loginPhone = new ShiroLoginPhone(authVo.getPhone());
        Dict dict = this.chatUserService.doLogin(loginPhone);
        return AjaxResult.success(dict);
    }

    /**
     * 找回密码（根据手机）
     */
    @IgnoreAuth
    @ApiVersion(VersionEnum.V1_0_0)
    @PostMapping("/forget")
    public AjaxResult forget(@Validated @RequestBody AuthVo04 authVo) {
        // 验证
        this.smsService.verifySms(authVo.getPhone(), authVo.getCode(), SmsTypeEnum.FORGET);
        // 查询用户
        ChatUser chatUser = this.chatUserService.queryByPhone(authVo.getPhone());
        if (chatUser == null) {
            throw new BaseException("手机号不存在");
        }
        // 重置密码
        this.chatUserService.resetPass(chatUser.getUserId(), authVo.getPassword());
        return AjaxResult.success("您的密码重置成功，请重新登录");
    }
}
