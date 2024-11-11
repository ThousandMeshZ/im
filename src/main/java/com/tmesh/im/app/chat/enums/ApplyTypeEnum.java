package com.tmesh.im.app.chat.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 申请类型枚举
 */
@Getter
public enum ApplyTypeEnum {

    /**
     * 好友
     */
    FRIEND("1", "好友"),
    /**
     * 群组
     */
    GROUP("2", "群组"),
    ;

    @EnumValue
    @JsonValue
    private String code;
    private String info;

    ApplyTypeEnum(String code, String info) {
        this.code = code;
        this.info = info;
    }

}
