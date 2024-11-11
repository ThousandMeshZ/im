package com.tmesh.im.app.collect.service;

import com.github.pagehelper.PageInfo;
import com.tmesh.im.app.collect.domain.ChatCollect;
import com.tmesh.im.app.collect.vo.CollectVo01;
import com.tmesh.im.common.web.service.BaseService;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 收藏表 服务层
 */
public interface ChatCollectService extends BaseService<ChatCollect> {

    /**
     * 新增收藏
     */
    void addCollect(CollectVo01 collectVo);

    /**
     * 删除收藏
     */
    void deleteCollect(Long collectId);

    /**
     * 列表
     */
    PageInfo collectList(ChatCollect collect);

}
