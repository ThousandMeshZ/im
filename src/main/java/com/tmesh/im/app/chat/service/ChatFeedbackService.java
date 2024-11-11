package com.tmesh.im.app.chat.service;


import com.tmesh.im.app.chat.domain.ChatFeedback;
import com.tmesh.im.app.chat.vo.MyVo04;
import com.tmesh.im.common.web.service.BaseService;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 建议反馈 服务层
 */
public interface ChatFeedbackService extends BaseService<ChatFeedback> {

    /**
     * 添加建议反馈
     */
    void addFeedback(MyVo04 myVo);

}
