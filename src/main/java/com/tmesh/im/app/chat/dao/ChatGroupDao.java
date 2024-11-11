package com.tmesh.im.app.chat.dao;

import com.tmesh.im.app.chat.domain.ChatGroup;
import com.tmesh.im.app.push.vo.PushParamVo;
import com.tmesh.im.common.web.dao.BaseDao;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 群组 数据库访问层
 */
@Repository
public interface ChatGroupDao extends BaseDao<ChatGroup> {

    /**
     * 查询列表
     */
    List<ChatGroup> queryList(ChatGroup chatGroup);

    /**
     * 查询用户
     */
    List<PushParamVo> queryFriendPushFrom(@Param("groupId") Long groupId, @Param("userId") Long userId);

}
