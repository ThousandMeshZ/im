package com.tmesh.im.app.collect.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tmesh.im.app.collect.enums.CollectTypeEnum;
import com.tmesh.im.common.web.domain.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 收藏表实体类
 */
@Data
@TableName("chat_collect")
@Accessors(chain = true) // 链式调用
public class ChatCollect extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 收藏类型
     */
    private CollectTypeEnum collectType;
    /**
     * 内容
     */
    private String content;
    /**
     * 创建时间
     */
    private Date createTime;

}
