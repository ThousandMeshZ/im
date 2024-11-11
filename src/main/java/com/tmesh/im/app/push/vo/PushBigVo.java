package com.tmesh.im.app.push.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 大消息
 */
@Data
@Accessors(chain = true) // 链式调用
public class PushBigVo {

    /**
     * 消息内容(消息Id)
     */
    private String content;

}
