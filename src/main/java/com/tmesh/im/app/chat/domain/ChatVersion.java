package com.tmesh.im.app.chat.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tmesh.im.common.web.domain.BaseEntity;
import lombok.Data;


/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 版本实体类
 */
@Data
@TableName("chat_version")
public class ChatVersion extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 版本
     */
    private String version;
    /**
     * 地址
     */
    private String url;
    /**
     * 内容
     */
    private String content;

}
