package com.tmesh.im.app.chat.service;

import com.tmesh.im.app.chat.domain.ChatGroupInfo;
import com.tmesh.im.common.enums.YesOrNoEnum;
import com.tmesh.im.common.web.service.BaseService;

import java.util.List;
import java.util.Map;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 群组信息 服务层实现
 */
public interface ChatGroupInfoService extends BaseService<ChatGroupInfo> {

    /**
     * 查询详情
     */
    ChatGroupInfo getGroupInfo(Long groupId, Long userId, YesOrNoEnum verify);

    /**
     * 删除缓存
     */
    void delGroupInfoCache(Long groupId, List<Long> userList);

    /**
     * 查询数量
     */
    Long countByGroup(Long groupId);

    /**
     * 查询用户id
     */
    List<Long> queryUserList(Long groupId);

    /**
     * 查询用户id
     */
    List<ChatGroupInfo> queryUserList(Long groupId, List<Long> userList);

    /**
     * 查询用户id
     */
    Map<Long, ChatGroupInfo> queryUserMap(Long groupId);

    /**
     * 通过群组删除
     */
    void delByGroup(Long groupId);

}

