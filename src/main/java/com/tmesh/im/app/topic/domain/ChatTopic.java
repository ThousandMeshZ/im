package com.tmesh.im.app.topic.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tmesh.im.app.topic.enums.TopicTypeEnum;
import com.tmesh.im.common.web.domain.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 主题实体类
 */
@Data
@TableName("chat_topic")
@Accessors(chain = true) // 链式调用
public class ChatTopic extends BaseEntity {

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
     * 类型
     */
    private TopicTypeEnum topicType;
    /**
     * 内容
     */
    private String content;
    /**
     * 经纬度
     */
    private String location;
    /**
     * 时间
     */
    private Date createTime;

}
