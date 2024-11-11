package com.tmesh.im.app.topic.service;


import com.tmesh.im.app.topic.domain.ChatTopicLike;
import com.tmesh.im.app.topic.vo.TopicVo05;
import com.tmesh.im.common.web.service.BaseService;

import java.util.List;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 帖子点赞 服务层
 */
public interface ChatTopicLikeService extends BaseService<ChatTopicLike> {

    /**
     * 删除帖子
     */
    void delByTopic(Long topicId);

    /**
     * 查询点赞信息
     */
    List<TopicVo05> queryTopicLike(Long topicId);

    /**
     * 查询点赞信息
     */
    ChatTopicLike queryUserLike(Long topicId, Long userId);

}
