package com.tmesh.im.app.topic.dao;

import com.tmesh.im.app.topic.domain.ChatTopicReply;
import com.tmesh.im.app.topic.vo.TopicVo06;
import com.tmesh.im.common.web.dao.BaseDao;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 帖子回复表 数据库访问层
 */
@Repository
public interface ChatTopicReplyDao extends BaseDao<ChatTopicReply> {

    /**
     * 查询列表
     */
    List<ChatTopicReply> queryList(ChatTopicReply chatTopicReply);

    /**
     * 根据帖子查询
     */
    List<TopicVo06> queryReplyList(@Param("userId") Long userId, @Param("topicId") Long topicId);
}
