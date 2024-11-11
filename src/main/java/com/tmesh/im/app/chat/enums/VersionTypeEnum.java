package com.tmesh.im.app.chat.enums;

import lombok.Getter;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 版本类型枚举
 */
@Getter
public enum VersionTypeEnum {

    /**
     * 用户协议
     */
    AGREEMENT(1L, "agreement"),
    /**
     * 安卓
     */
    ANDROID(2L, "android"),
    /**
     * iOS
     */
    IOS(3L, "iOS"),
    ;

    private Long code;
    private String name;

    VersionTypeEnum(Long code, String name) {
        this.code = code;
        this.name = name;
    }

}
