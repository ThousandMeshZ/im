package com.tmesh.im.app.topic.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tmesh.im.common.enums.YesOrNoEnum;
import com.tmesh.im.common.web.domain.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 帖子点赞实体类
 */
@Data
@TableName("chat_topic_like")
@Accessors(chain = true) // 链式调用
public class ChatTopicLike extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 帖子id
     */
    private Long topicId;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 是否点赞
     */
    private YesOrNoEnum hasLike;

}
