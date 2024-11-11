package com.tmesh.im.app.chat.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tmesh.im.common.web.domain.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 群组实体类
 */
@Data
@TableName("chat_group")
@Accessors(chain = true) // 链式调用
public class ChatGroup extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 群名
     */
    private String name;
    /**
     * 公告
     */
    private String notice;
    /**
     * 头像
     */
    private String portrait;
    /**
     * 群主
     */
    private Long master;
    /**
     * 创建时间
     */
    private Date createTime;

}
