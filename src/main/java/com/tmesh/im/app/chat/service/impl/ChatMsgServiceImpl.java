package com.tmesh.im.app.chat.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.tmesh.im.app.chat.dao.ChatMsgDao;
import com.tmesh.im.app.chat.domain.*;
import com.tmesh.im.app.chat.enums.FriendTypeEnum;
import com.tmesh.im.app.chat.enums.MsgStatusEnum;
import com.tmesh.im.app.chat.service.*;
import com.tmesh.im.app.chat.vo.ChatVo01;
import com.tmesh.im.app.chat.vo.ChatVo02;
import com.tmesh.im.app.chat.vo.ChatVo03;
import com.tmesh.im.app.chat.vo.ChatVo04;
import com.tmesh.im.app.push.enums.PushMsgEnum;
import com.tmesh.im.app.push.enums.PushTalkEnum;
import com.tmesh.im.app.push.service.ChatPushService;
import com.tmesh.im.app.push.vo.PushParamVo;
import com.tmesh.im.common.constant.AppConstants;
import com.tmesh.im.common.enums.YesOrNoEnum;
import com.tmesh.im.common.shiro.ShiroUtils;
import com.tmesh.im.common.utils.TimerUtils;
import com.tmesh.im.common.web.service.impl.BaseServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 聊天消息 服务层实现
 */
@Service("chatMsgService")
public class ChatMsgServiceImpl extends BaseServiceImpl<ChatMsg> implements ChatMsgService {

    @Resource
    private ChatMsgDao chatMsgDao;

    @Resource
    private ChatFriendService friendService;

    @Resource
    private ChatGroupService groupService;

    @Resource
    private ChatGroupInfoService groupInfoService;

    @Resource
    private ChatPushService chatPushService;

    @Resource
    private ChatUserService chatUserService;

    @Resource
    private ChatTalkService chatTalkService;

    @Autowired
    public void setBaseDao() {
        super.setBaseDao(this.chatMsgDao);
    }

    @Override
    public List<ChatMsg> queryList(ChatMsg t) {
        return this.chatMsgDao.queryList(t);
    }

    @Transactional
    @Override
    public ChatVo03 sendFriendMsg(ChatVo01 chatVo) {
        Long userId = ShiroUtils.getUserId();
        Long friendId = chatVo.getUserId();
        // 系统好友
        if (friendId.equals(10002L) || friendId.equals(10003L)) {
            return this.sys(chatVo);
        }
        // 自己给自己发消息
        if (userId.equals(friendId)) {
            return this.self(chatVo);
        }
        // 发送给好友的消息
        return this.friend(chatVo);
    }

    /**
     * 保存消息
     */
    private ChatMsg saveMsg(ChatVo01 chatVo) {
        Long userId = ShiroUtils.getUserId();
        ChatMsg chatMsg = new ChatMsg()
                .setFromId(userId)
                .setToId(chatVo.getUserId())
                .setMsgType(chatVo.getMsgType())
                .setTalkType(PushTalkEnum.SINGLE)
                .setContent(chatVo.getContent())
                .setCreateTime(DateUtil.date());
        this.add(chatMsg);
        return chatMsg;
    }

    /**
     * 系统消息
     */
    private ChatVo03 sys(ChatVo01 chatVo) {
        // 保存消息
        ChatMsg chatMsg = this.saveMsg(chatVo);
        Long userId = ShiroUtils.getUserId();
        Long friendId = chatVo.getUserId();
        String content = chatVo.getContent();
        // 异步执行
        TimerUtils.instance().addTask(timeout -> {
            // 发送聊天
            PushParamVo paramVo = this.chatTalkService.talk(friendId, content);
            if (paramVo == null) {
                return;
            }
            // 推送
            this.chatPushService.pushMsg(paramVo.setToId(userId).setMsgId(IdWorker.getId()), PushMsgEnum.TEXT);
        },2 , TimeUnit.SECONDS);
        // 返回结果
        return this.doResult(MsgStatusEnum.NORMAL)
                .setMsgId(chatMsg.getId());
    }

    /**
     * 自己消息
     */
    private ChatVo03 self(ChatVo01 chatVo) {
        // 保存消息
        ChatMsg chatMsg = this.saveMsg(chatVo);
        Long userId = ShiroUtils.getUserId();
        Long friendId = chatVo.getUserId();
        String content = chatVo.getContent();
        PushMsgEnum msgType = chatVo.getMsgType();
        // 组装推送
        PushParamVo paramVo = ChatUser.initParam(this.chatUserService.getById(userId))
                .setUserType(FriendTypeEnum.SELF)
                .setContent(content)
                .setToId(friendId)
                .setMsgId(IdWorker.getId());
        // 推送
        // 异步执行
        TimerUtils.instance().addTask(timeout -> 
                // 推送
                this.chatPushService.pushMsg(paramVo, msgType)
        , 2, TimeUnit.SECONDS);
        // 返回结果
        return this.doResult(MsgStatusEnum.NORMAL)
                .setMsgId(chatMsg.getId());
    }

    /**
     * 好友消息
     */
    private ChatVo03 friend(ChatVo01 chatVo) {
        Long userId = ShiroUtils.getUserId();
        Long friendId = chatVo.getUserId();
        String content = chatVo.getContent();
        PushMsgEnum msgType = chatVo.getMsgType();
        // 校验好友
        ChatFriend friend1 = this.friendService.getFriend(userId, friendId);
        if (friend1 == null) {
            return this.doResult(MsgStatusEnum.FRIEND_TO);
        }
        // 校验好友
        ChatFriend friend2 = friendService.getFriend(friendId, userId);
        if (friend2 == null) {
            return this.doResult(MsgStatusEnum.FRIEND_FROM);
        }
        // 校验黑名单
        if (YesOrNoEnum.YES.equals(friend2.getBlack())) {
            return this.doResult(MsgStatusEnum.FRIEND_BLACK);
        }
        // 校验好友
        ChatUser toUser = chatUserService.getById(friendId);
        if (toUser == null) {
            return this.doResult(MsgStatusEnum.FRIEND_DELETED);
        }
        // 保存消息
        ChatMsg chatMsg = this.saveMsg(chatVo);
        // 组装推送
        PushParamVo paramVo = ChatUser.initParam(this.chatUserService.getById(userId))
                .setNickName(friend2.getRemark())
                .setTop(friend2.getTop())
                .setContent(content)
                .setToId(friendId)
                .setMsgId(chatMsg.getId());
        ChatVo04 chatVo04 = null;
        if (PushMsgEnum.TRTC_VOICE_START.equals(msgType) 
                || PushMsgEnum.TRTC_VIDEO_START.equals(msgType)) {
            chatVo04 = new ChatVo04()
                    .setUserId(friendId)
                    .setTrtcId(AppConstants.REDIS_TRTC_USER + friendId)
                    .setPortrait(toUser.getPortrait())
                    .setNickName(friend1.getRemark());
        }
        // 推送
        this.chatPushService.pushMsg(paramVo, msgType);
        return this.doResult(MsgStatusEnum.NORMAL)
                .setMsgId(chatMsg.getId())
                .setUserInfo(chatVo04);
    }

    /**
     * 返回发送结果
     */
    private ChatVo03 doResult(MsgStatusEnum status) {
        return new ChatVo03().setStatus(status);
    }

    @Override
    public ChatVo03 sendGroupMsg(ChatVo02 chatVo) {
        String content = chatVo.getContent();
        Long fromId = ShiroUtils.getUserId();
        Long groupId = chatVo.getGroupId();
        // 查询群组
        ChatGroup group = this.groupService.getById(groupId);
        if (group == null) {
            return this.doResult(MsgStatusEnum.GROUP_NOT_EXIST);
        }
        // 查询群明细
        ChatGroupInfo groupInfo = this.groupInfoService.getGroupInfo(groupId, fromId, YesOrNoEnum.NO);
        if (groupInfo == null) {
            return this.doResult(MsgStatusEnum.GROUP_INFO_NOT_EXIST);
        }
        // 保存数据
        ChatMsg chatMsg = new ChatMsg()
                .setFromId(fromId)
                .setToId(groupId)
                .setMsgType(chatVo.getMsgType())
                .setTalkType(PushTalkEnum.GROUP)
                .setContent(content)
                .setCreateTime(DateUtil.date());
        this.add(chatMsg);
        // 查询群列表
        List<PushParamVo> userList = this.groupService.queryFriendPushFrom(chatMsg);
        // 群信息
        PushParamVo groupUser = new PushParamVo()
                .setUserId(group.getId())
                .setNickName(group.getName())
                .setPortrait(group.getPortrait());
        // 推送
        this.chatPushService.pushGroupMsg(userList, groupUser, chatVo.getMsgType());
        return this.doResult(MsgStatusEnum.NORMAL)
                .setMsgId(chatMsg.getId());
    }
}
