package com.tmesh.im.app.push.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 消息发送人
 */
@Data
@Accessors(chain = true) // 链式调用
public class PushFromVo {

    /**
     * 发送人
     */
    private String userId;

    /**
     * 用户头像
     */
    private String portrait;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 用户类型（normal、self、weather、translation）
     */
    private String userType;

}
