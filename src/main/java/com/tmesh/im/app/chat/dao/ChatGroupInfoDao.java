package com.tmesh.im.app.chat.dao;

import com.tmesh.im.app.chat.domain.ChatGroupInfo;
import com.tmesh.im.common.web.dao.BaseDao;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 群组信息 数据库访问层
 */
@Repository
public interface ChatGroupInfoDao extends BaseDao<ChatGroupInfo> {

    /**
     * 查询列表
     */
    List<ChatGroupInfo> queryList(ChatGroupInfo chatGroupInfo);

}
