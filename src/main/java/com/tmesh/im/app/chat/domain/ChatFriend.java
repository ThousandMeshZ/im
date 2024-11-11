package com.tmesh.im.app.chat.domain;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tmesh.im.app.chat.enums.ApplySourceEnum;
import com.tmesh.im.common.enums.YesOrNoEnum;
import com.tmesh.im.common.web.domain.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 好友表实体类
 */
@Data
@TableName("chat_friend")
@Accessors(chain = true) // 链式调用
public class ChatFriend extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 用户id
     */
    private Long fromId;
    /**
     * 好友id
     */
    private Long toId;
    /**
     * 备注
     */
    private String remark;
    /**
     * 是否黑名单
     */
    private YesOrNoEnum black;
    /**
     * 是否置顶
     */
    private YesOrNoEnum top;
    /**
     * 申请来源
     */
    private ApplySourceEnum source;
    /**
     * 创建时间
     */
    @TableField(updateStrategy = FieldStrategy.NEVER)
    private Date createTime;

}
