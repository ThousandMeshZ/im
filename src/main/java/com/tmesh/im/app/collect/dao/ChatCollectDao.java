package com.tmesh.im.app.collect.dao;

import com.tmesh.im.app.collect.domain.ChatCollect;
import com.tmesh.im.common.web.dao.BaseDao;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 收藏表 数据库访问层
 */
@Repository
public interface ChatCollectDao extends BaseDao<ChatCollect> {

    /**
     * 查询列表
     */
    List<ChatCollect> queryList(ChatCollect chatCollect);

}
