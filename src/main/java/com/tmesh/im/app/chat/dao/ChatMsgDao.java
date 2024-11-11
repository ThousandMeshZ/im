package com.tmesh.im.app.chat.dao;

import com.tmesh.im.app.chat.domain.ChatMsg;
import com.tmesh.im.common.web.dao.BaseDao;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 聊天消息 数据库访问层
 */
@Repository
public interface ChatMsgDao extends BaseDao<ChatMsg> {

    /**
     * 查询列表
     */
    List<ChatMsg> queryList(ChatMsg chatMsg);

}
