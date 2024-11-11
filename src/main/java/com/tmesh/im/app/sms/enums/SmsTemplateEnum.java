package com.tmesh.im.app.sms.enums;

import lombok.Getter;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 短信模板枚举
 */
@Getter
public enum SmsTemplateEnum {

    /**
     * 验证码-00
     */
    VERIFY_CODE("SMS_209335004", "验证码-00"),
    ;

    private String code;
    private String info;

    SmsTemplateEnum(String code, String info) {
        this.code = code;
        this.info = info;
    }

}
