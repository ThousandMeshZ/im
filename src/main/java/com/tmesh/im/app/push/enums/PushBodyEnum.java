package com.tmesh.im.app.push.enums;

import lombok.Getter;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 推送消息类型
 */
@Getter
public enum PushBodyEnum {

    /**
     * 普通消息
     */
    MSG("MSG", "普通消息"),
    /**
     * 通知消息
     */
    NOTICE("NOTICE", "通知消息"),
    ;

    private String code;
    private String info;

    PushBodyEnum(String code, String info) {
        this.code = code;
        this.info = info;
    }

}
