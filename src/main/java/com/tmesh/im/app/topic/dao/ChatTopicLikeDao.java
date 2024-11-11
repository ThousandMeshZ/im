package com.tmesh.im.app.topic.dao;

import com.tmesh.im.app.topic.domain.ChatTopicLike;
import com.tmesh.im.app.topic.vo.TopicVo05;
import com.tmesh.im.common.web.dao.BaseDao;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 帖子点赞 数据库访问层
 */
@Repository
public interface ChatTopicLikeDao extends BaseDao<ChatTopicLike> {

    /**
     * 查询列表
     */
    List<ChatTopicLike> queryList(ChatTopicLike chatTopicLike);

    /**
     * 查询点赞信息
     */
    List<TopicVo05> queryTopicLike(@Param("topicId") Long topicId, @Param("userId") Long userId);
}
