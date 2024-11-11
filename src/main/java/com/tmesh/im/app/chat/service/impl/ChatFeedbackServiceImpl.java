package com.tmesh.im.app.chat.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.tmesh.im.app.chat.dao.ChatFeedbackDao;
import com.tmesh.im.app.chat.domain.ChatFeedback;
import com.tmesh.im.app.chat.service.ChatFeedbackService;
import com.tmesh.im.app.chat.vo.MyVo04;
import com.tmesh.im.common.constant.HeadConstant;
import com.tmesh.im.common.shiro.ShiroUtils;
import com.tmesh.im.common.utils.ServletUtils;
import com.tmesh.im.common.web.service.impl.BaseServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 建议反馈 服务层实现类
 */
@Service("chatFeedbackService")
public class ChatFeedbackServiceImpl extends BaseServiceImpl<ChatFeedback> implements ChatFeedbackService {

    @Resource
    private ChatFeedbackDao chatFeedbackDao;

    @Autowired
    public void setBaseDao() {
        super.setBaseDao(this.chatFeedbackDao);
    }

    @Override
    public List<ChatFeedback> queryList(ChatFeedback t) {
        return this.chatFeedbackDao.queryList(t);
    }

    @Override
    public void addFeedback(MyVo04 myVo) {
        String version = ServletUtils.getRequest().getHeader(HeadConstant.VERSION);
        ChatFeedback feedback = BeanUtil.toBean(myVo, ChatFeedback.class)
                .setUserId(ShiroUtils.getUserId())
                .setVersion(version)
                .setCreateTime(DateUtil.date());
        this.add(feedback);
    }
}