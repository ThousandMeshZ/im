package com.tmesh.im.app.chat.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 申请状态枚举
 */
@Getter
public enum ApplyStatusEnum {

    /**
     * 无
     */
    NONE("0", "无"),
    /**
     * 同意
     */
    AGREE("1", "同意"),
    /**
     * 拒绝
     */
    REFUSED("2", "拒绝"),
    /**
     * 忽略
     */
    IGNORE("3", "忽略"),
    ;

    @EnumValue
    @JsonValue
    private String code;
    private String info;

    ApplyStatusEnum(String code, String info) {
        this.code = code;
        this.info = info;
    }

}
