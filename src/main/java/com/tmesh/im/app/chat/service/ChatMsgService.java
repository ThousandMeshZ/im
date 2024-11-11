package com.tmesh.im.app.chat.service;


import com.tmesh.im.app.chat.domain.ChatMsg;
import com.tmesh.im.app.chat.vo.ChatVo01;
import com.tmesh.im.app.chat.vo.ChatVo02;
import com.tmesh.im.app.chat.vo.ChatVo03;
import com.tmesh.im.common.web.service.BaseService;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 聊天消息 服务层
 */
public interface ChatMsgService extends BaseService<ChatMsg> {

    /**
     * 发送消息
     */
    ChatVo03 sendFriendMsg(ChatVo01 chatVo);

    /**
     * 发送群消息
     */
    ChatVo03 sendGroupMsg(ChatVo02 chatVo);

}
