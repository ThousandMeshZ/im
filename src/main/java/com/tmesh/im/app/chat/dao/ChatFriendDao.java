package com.tmesh.im.app.chat.dao;

import com.tmesh.im.app.chat.domain.ChatFriend;
import com.tmesh.im.app.chat.vo.FriendVo06;
import com.tmesh.im.common.web.dao.BaseDao;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 好友 数据库访问层
 */
@Repository
public interface ChatFriendDao extends BaseDao<ChatFriend> {

    /**
     * 查询列表
     */
    List<ChatFriend> queryList(ChatFriend chatFriend);

    /**
     * 查询好友列表
     */
    List<FriendVo06> friendList(Long userId);

    /**
     * 查询好友id
     */
    List<Long> queryFriendId(Long userId);

}