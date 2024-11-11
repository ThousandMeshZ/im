package com.tmesh.im.app.sms.service.impl;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.RandomUtil;
import com.tmesh.im.app.sms.enums.SmsTemplateEnum;
import com.tmesh.im.app.sms.enums.SmsTypeEnum;
import com.tmesh.im.app.sms.service.SmsService;
import com.tmesh.im.app.sms.vo.SmsVo;
import com.tmesh.im.common.config.PlatformConfig;
import com.tmesh.im.common.enums.YesOrNoEnum;
import com.tmesh.im.common.exception.BaseException;
import com.tmesh.im.common.redis.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 短信 服务层实现类
 */
@Service("smsService")
@Slf4j
public class SmsServiceImpl implements SmsService {

    @Autowired
    private RedisUtils redisUtils;
    
    @Override
    public Dict sendSms(SmsVo smsVo) {
        // 验证手机号
        if (!Validator.isMobile(smsVo.getPhone())) {
            throw new BaseException("请输入正确的手机号");
        }
        SmsTypeEnum smsType = smsVo.getType();
        String key = smsType.getPrefix().concat(smsVo.getPhone());
        // 生成验证码
        String code = String.valueOf(RandomUtil.randomInt(1000, 9999));
        // 发送短信
        if (YesOrNoEnum.YES.equals(PlatformConfig.SMS)) {
            Dict dict = Dict.create().set("code", code);
            this.doSendSms(smsVo.getPhone(), SmsTemplateEnum.VERIFY_CODE, dict);
        }
        // 存入缓存
        this.redisUtils.set(key, code, smsType.getTimeout(), TimeUnit.MINUTES);
        return Dict.create().set("code", code).set("expiration", smsType.getTimeout());
    }
    
    @Override
    public void verifySms(String phone, String code, SmsTypeEnum smsType) {
        // 验证手机号
        if (!Validator.isMobile(phone)) {
            throw new BaseException("请输入正确的手机号");
        }
        String key = smsType.getPrefix().concat(phone);
        if (!this.redisUtils.hasKey(key)) {
            throw new BaseException("验证码已过期，请重新获取");
        }
        String value = this.redisUtils.get(key);
        if (value.equalsIgnoreCase(code)) {
            this.redisUtils.delete(key);
        } else {
            throw new BaseException("验证码buzq，请重新获取");
        }
    }

    /**
     * 执行发送短信
     *
     * @param phone
     * @param templateCode
     * @param dict
     */
    @Override
    public void doSendSms(String phone, SmsTemplateEnum templateCode, Dict dict) {
        //TODO 短信待集成
    }
}
