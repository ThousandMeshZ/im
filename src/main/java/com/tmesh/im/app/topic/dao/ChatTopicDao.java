package com.tmesh.im.app.topic.dao;

import com.tmesh.im.app.topic.domain.ChatTopic;
import com.tmesh.im.app.topic.vo.TopicVo04;
import com.tmesh.im.common.web.dao.BaseDao;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 主题 数据库访问层
 */
@Repository
public interface ChatTopicDao extends BaseDao<ChatTopic> {

    /**
     * 查询列表
     */
    List<ChatTopic> queryList(ChatTopic chatTopic);

    /**
     * 查询
     */
    List<TopicVo04> topicList(Long userId);

}
