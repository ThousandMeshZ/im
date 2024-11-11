package com.tmesh.im.app.topic.service;

import com.github.pagehelper.PageInfo;
import com.tmesh.im.app.topic.domain.ChatTopic;
import com.tmesh.im.app.topic.vo.*;
import com.tmesh.im.common.web.service.BaseService;

import java.util.List;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 主题 服务层
 */
public interface ChatTopicService extends BaseService<ChatTopic> {

    /**
     * 发布帖子
     */
    void sendTopic(TopicVo01 topicVo);

    /**
     * 删除帖子
     */
    void delTopic(Long topicId);

    /**
     * 指定人的帖子
     */
    PageInfo userTopic(Long friendId);

    /**
     * 好友的帖子
     */
    PageInfo topicList();

    /**
     * 朋友圈详情
     */
    TopicVo03 topicInfo(Long topicId);

    /**
     * 查询通知列表
     */
    List<TopicVo09> queryNoticeList();

    /**
     * 清空通知列表
     */
    void clearNotice();

    /**
     * 点赞
     */
    void like(Long topicId);

    /**
     * 取消点赞
     */
    void cancelLike(Long topicId);

    /**
     * 回复
     */
    TopicVo06 reply(TopicVo07 topicVo);

    /**
     * 删除回复
     */
    void delReply(Long replyId);
}
