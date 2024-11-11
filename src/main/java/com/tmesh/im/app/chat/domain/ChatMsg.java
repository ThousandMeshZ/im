package com.tmesh.im.app.chat.domain;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tmesh.im.app.push.enums.PushMsgEnum;
import com.tmesh.im.app.push.enums.PushTalkEnum;
import com.tmesh.im.common.web.domain.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 聊天消息实体类
 */
@Data
@TableName("chat_msg")
@Accessors(chain = true) // 链式调用
public class ChatMsg extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 消息主键
     */
    @TableId
    private Long id;
    /**
     * 发送人
     */
    private Long fromId;
    /**
     * 接收人
     */
    private Long toId;
    /**
     * 消息类型
     */
    private PushMsgEnum msgType;
    /**
     * 消息类型
     */
    private PushTalkEnum talkType;
    /**
     * 消息内容
     */
    private String content;
    /**
     * 创建时间
     */
    @TableField(updateStrategy = FieldStrategy.NEVER)
    private Date createTime;

}
