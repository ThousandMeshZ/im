package com.tmesh.im.app.topic.service;

import com.tmesh.im.app.topic.domain.ChatTopicReply;
import com.tmesh.im.app.topic.vo.TopicVo06;
import com.tmesh.im.common.web.service.BaseService;

import java.util.List;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 帖子回复表 服务层
 */
public interface ChatTopicReplyService extends BaseService<ChatTopicReply> {

    /**
     * 根据帖子id删除
     */
    void delByTopic(Long topicId);

    /**
     * 根据帖子查询
     */
    List<TopicVo06> queryReplyList(Long topicId);

}
