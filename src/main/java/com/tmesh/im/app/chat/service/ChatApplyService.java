package com.tmesh.im.app.chat.service;

import com.github.pagehelper.PageInfo;
import com.tmesh.im.app.chat.domain.ChatApply;
import com.tmesh.im.app.chat.enums.ApplySourceEnum;
import com.tmesh.im.app.chat.vo.ApplyVo03;
import com.tmesh.im.common.web.service.BaseService;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 好友申请 服务层
 */
public interface ChatApplyService extends BaseService<ChatApply> {

    /**
     * 申请好友
     */
    void applyFriend(Long acceptId, ApplySourceEnum source, String reason);

    /**
     * 申请记录
     */
    PageInfo list();

    /**
     * 查询详情
     */
    ApplyVo03 getInfo(Long applyId);
}
