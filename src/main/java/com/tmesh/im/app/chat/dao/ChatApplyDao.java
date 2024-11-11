package com.tmesh.im.app.chat.dao;

import com.tmesh.im.app.chat.domain.ChatApply;
import com.tmesh.im.common.web.dao.BaseDao;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 好友申请 数据库访问层
 */
@Repository
public interface ChatApplyDao extends BaseDao<ChatApply> {

    /**
     * 查询列表
     */
    List<ChatApply> queryList(ChatApply chatApply);

}
