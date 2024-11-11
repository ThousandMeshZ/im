package com.tmesh.im.app.topic.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 通知类型枚举
 */
@Getter
public enum TopicNoticeTypeEnum {

    /**
     * 点赞
     */
    LIKE("1", "点赞"),
    /**
     * 回复
     */
    REPLY("2", "回复"),
    ;

    @EnumValue
    @JsonValue
    private String code;
    private String name;

    TopicNoticeTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

}
