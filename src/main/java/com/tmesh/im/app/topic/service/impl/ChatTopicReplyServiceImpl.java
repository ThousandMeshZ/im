package com.tmesh.im.app.topic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tmesh.im.app.chat.domain.ChatUser;
import com.tmesh.im.app.topic.dao.ChatTopicReplyDao;
import com.tmesh.im.app.topic.domain.ChatTopicReply;
import com.tmesh.im.app.topic.enums.TopicReplyTypeEnum;
import com.tmesh.im.app.topic.service.ChatTopicReplyService;
import com.tmesh.im.app.topic.vo.TopicVo06;
import com.tmesh.im.common.enums.YesOrNoEnum;
import com.tmesh.im.common.shiro.ShiroUtils;
import com.tmesh.im.common.web.service.impl.BaseServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 帖子回复表 服务层实现类
 */
@Service("chatTopicReplyService")
public class ChatTopicReplyServiceImpl extends BaseServiceImpl<ChatTopicReply> implements ChatTopicReplyService {

    @Resource
    private ChatTopicReplyDao chatTopicReplyDao;

    @Autowired
    public void setBaseDao() {
        super.setBaseDao(this.chatTopicReplyDao);
    }

    @Override
    public List<ChatTopicReply> queryList(ChatTopicReply t) {
        return this.chatTopicReplyDao.queryList(t);
    }

    @Override
    public void delByTopic(Long topicId) {
        this.chatTopicReplyDao.delete(new QueryWrapper<>(new ChatTopicReply()
                .setTopicId(topicId)));
    }

    @Override
    public List<TopicVo06> queryReplyList(Long topicId) {
        Long userId = ShiroUtils.getUserId();
        List<TopicVo06> dataList = this.chatTopicReplyDao.queryReplyList(userId, topicId);
        ChatUser chatUser = ChatUser.initUser(null);
        dataList.forEach(topicVo -> {
            // 是否可以删除
            topicVo.setCanDeleted(userId.equals(topicVo.getUserId()) ? YesOrNoEnum.YES : YesOrNoEnum.NO);
            // 纠正注销用户
            if (!StringUtils.hasText(topicVo.getPortrait())) {
                topicVo.setUserId(chatUser.getUserId());
                topicVo.setNickName(chatUser.getNickName());
                topicVo.setPortrait(chatUser.getPortrait());
            }
            // 纠正注销用户
            if (TopicReplyTypeEnum.USER.equals(topicVo.getReplyType()) && !StringUtils.hasText(topicVo.getToPortrait())) {
                topicVo.setToNickName(chatUser.getNickName());
                topicVo.setToPortrait(chatUser.getPortrait());
            }
        });
        return dataList;
    }
}
