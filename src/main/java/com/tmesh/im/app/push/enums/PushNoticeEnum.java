package com.tmesh.im.app.push.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 消息类型枚举
 */
@Getter
public enum PushNoticeEnum {

    /**
     * 帖子_小红点
     */
    TOPIC_RED("TOPIC_RED", "帖子_小红点"),
    /**
     * 帖子_回复
     */
    TOPIC_REPLY("TOPIC_REPLY", "帖子_回复"),
    /**
     * 好友_申请
     */
    FRIEND_APPLY("FRIEND_APPLY", "好友_申请"),
    ;

    @EnumValue
    @JsonValue
    private String code;
    private String info;

    PushNoticeEnum(String code, String info) {
        this.code = code;
        this.info = info;
    }

}
