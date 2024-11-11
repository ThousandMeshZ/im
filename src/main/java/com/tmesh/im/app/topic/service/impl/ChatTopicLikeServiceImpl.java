package com.tmesh.im.app.topic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tmesh.im.app.topic.dao.ChatTopicLikeDao;
import com.tmesh.im.app.topic.domain.ChatTopicLike;
import com.tmesh.im.app.topic.service.ChatTopicLikeService;
import com.tmesh.im.app.topic.vo.TopicVo05;
import com.tmesh.im.common.shiro.ShiroUtils;
import com.tmesh.im.common.web.service.impl.BaseServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 帖子点赞 服务层实现类
 */
@Service("chatTopicLikeService")
public class ChatTopicLikeServiceImpl extends BaseServiceImpl<ChatTopicLike> implements ChatTopicLikeService {

    @Resource
    private ChatTopicLikeDao chatTopicLikeDao;

    @Autowired
    public void setBaseDao() {
        super.setBaseDao(this.chatTopicLikeDao);
    }

    @Override
    public List<ChatTopicLike> queryList(ChatTopicLike t) {
        return this.chatTopicLikeDao.queryList(t);
    }


    @Override
    public void delByTopic(Long topicId) {
        ChatTopicLike query = new ChatTopicLike()
                .setTopicId(topicId);
        this.chatTopicLikeDao.delete(new QueryWrapper<>(query));
    }

    @Override
    public List<TopicVo05> queryTopicLike(Long topicId) {
        Long userId = ShiroUtils.getUserId();
        return this.chatTopicLikeDao.queryTopicLike(topicId, userId);
    }

    @Override
    public ChatTopicLike queryUserLike(Long topicId, Long userId) {
        ChatTopicLike query = new ChatTopicLike()
                .setTopicId(topicId)
                .setUserId(userId);
        return this.queryOne(query);
    }
}
