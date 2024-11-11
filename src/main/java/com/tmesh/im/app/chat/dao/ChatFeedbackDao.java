package com.tmesh.im.app.chat.dao;

import com.tmesh.im.app.chat.domain.ChatFeedback;
import com.tmesh.im.common.web.dao.BaseDao;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 建议反馈 数据库访问层
 */
@Repository
public interface ChatFeedbackDao extends BaseDao<ChatFeedback> {

    /**
     * 查询列表
     */
    List<ChatFeedback> queryList(ChatFeedback chatFeedback);

}
