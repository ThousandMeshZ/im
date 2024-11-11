package com.tmesh.im.app.chat.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tmesh.im.app.chat.enums.ApplySourceEnum;
import com.tmesh.im.app.chat.enums.ApplyStatusEnum;
import com.tmesh.im.app.chat.enums.ApplyTypeEnum;
import com.tmesh.im.common.web.domain.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.util.Date;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 好友申请表实体类
 */
@Data
@TableName("chat_apply")
@Accessors(chain = true) // 链式调用
public class ChatApply extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 发起 id
     */
    private Long fromId;
    /**
     * 接收 id
     */
    private Long toId;
    /**
     * 目标 id
     */
    private Long targetId;
    /**
     * 申请类型 1好友 2群组
     */
    private ApplyTypeEnum applyType;
    /**
     * 申请状态 0无 1同意 2拒绝
     */
    private ApplyStatusEnum applyStatus;
    /**
     * 申请来源
     */
    private ApplySourceEnum applySource;
    /**
     * 理由
     */
    private String reason;
    /**
     * 申请时间
     */
    private Date createTime;

}
