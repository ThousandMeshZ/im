package com.tmesh.im.app.push.vo;

import com.tmesh.im.app.chat.enums.FriendTypeEnum;
import com.tmesh.im.common.enums.YesOrNoEnum;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 消息发送人视图
 */
@Data
@Accessors(chain = true) // 链式调用
public class PushParamVo {

    /**
     * 消息id
     */
    private Long msgId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户头像
     */
    private String portrait;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 发送内容
     */
    private String content;

    /**
     * 是否静默
     */
    private YesOrNoEnum disturb = YesOrNoEnum.NO;

    /**
     * 是否置顶
     */
    private YesOrNoEnum top = YesOrNoEnum.NO;

    /**
     * 好友类型
     */
    private FriendTypeEnum userType = FriendTypeEnum.NORMAL;

    /**
     * 临时参数
     */
    private Long toId;

}
