package com.tmesh.im.app.chat.dao;

import com.tmesh.im.app.chat.domain.ChatVersion;
import com.tmesh.im.common.web.dao.BaseDao;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 版本 数据库访问层
 */
@Repository
public interface ChatVersionDao extends BaseDao<ChatVersion> {

    /**
     * 查询列表
     */
    List<ChatVersion> queryList(ChatVersion chatVersion);

}
