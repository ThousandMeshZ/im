package com.tmesh.im.app.chat.dao;

import com.tmesh.im.app.chat.domain.ChatUser;
import com.tmesh.im.common.web.dao.BaseDao;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 用户表 数据库访问层
 */
@Repository
public interface ChatUserDao extends BaseDao<ChatUser> {

    /**
     * 查询列表
     */
    List<ChatUser> queryList(ChatUser chatUser);

}