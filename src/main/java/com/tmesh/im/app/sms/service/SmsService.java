package com.tmesh.im.app.sms.service;

import cn.hutool.core.lang.Dict;
import com.tmesh.im.app.sms.enums.SmsTemplateEnum;
import com.tmesh.im.app.sms.enums.SmsTypeEnum;
import com.tmesh.im.app.sms.vo.SmsVo;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 短信 服务层
 */
public interface SmsService {

    /**
     * 发送短信
     */
    Dict sendSms(SmsVo smsVo);

    /**
     * 验证短信
     */
    void verifySms(String phone, String code, SmsTypeEnum type);

    void doSendSms(String phone, SmsTemplateEnum templateCode, Dict dict);

}
