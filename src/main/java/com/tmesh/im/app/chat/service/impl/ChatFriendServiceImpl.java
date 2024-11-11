package com.tmesh.im.app.chat.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.PatternPool;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.tmesh.im.app.chat.dao.ChatFriendDao;
import com.tmesh.im.app.chat.domain.*;
import com.tmesh.im.app.chat.enums.ApplySourceEnum;
import com.tmesh.im.app.chat.enums.ApplyStatusEnum;
import com.tmesh.im.app.chat.enums.ApplyTypeEnum;
import com.tmesh.im.app.chat.enums.FriendTypeEnum;
import com.tmesh.im.app.chat.service.*;
import com.tmesh.im.app.chat.vo.*;
import com.tmesh.im.app.push.enums.PushMsgEnum;
import com.tmesh.im.app.push.service.ChatPushService;
import com.tmesh.im.app.push.vo.PushParamVo;
import com.tmesh.im.common.constant.AppConstants;
import com.tmesh.im.common.enums.YesOrNoEnum;
import com.tmesh.im.common.exception.BaseException;
import com.tmesh.im.common.redis.RedisUtils;
import com.tmesh.im.common.shiro.ShiroUtils;
import com.tmesh.im.common.web.service.impl.BaseServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 好友 服务层实现
 */
public class ChatFriendServiceImpl extends BaseServiceImpl<ChatFriend> implements ChatFriendService {

    @Resource
    private ChatFriendDao chatFriendDao;

    @Resource
    @Lazy
    private ChatUserService chatUserService;

    @Resource
    private ChatApplyService chatApplyService;

    @Resource
    private ChatPushService chatPushService;

    @Resource
    private ChatGroupService groupService;

    @Resource
    private ChatGroupInfoService groupInfoService;

    @Resource
    private ChatTalkService chatTalkService;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    public void setBaseDao() {
        super.setBaseDao(this.chatFriendDao);
    }

    @Override
    public List<ChatFriend> queryList(ChatFriend t) {
        List<ChatFriend> dataList = this.chatFriendDao.queryList(t);
        return dataList;
    }

    @Override
    public FriendVo07 findFriend(String param) {
        // 好友
        ChatUser chatUser;
        // 来源
        ApplySourceEnum sourceEnum = null;
        // 按扫码加好友
        if (StrUtil.startWith(param, AppConstants.QR_CODE_USER)) {
            Long userId = Convert.toLong(ReUtil.get(PatternPool.NUMBERS, param, 0), null);
            chatUser = this.chatUserService.getById(userId);
            sourceEnum = ApplySourceEnum.SCAN;
        }
        // 按手机搜索
        else if ((chatUser = this.chatUserService.queryByPhone(param)) != null) {
            sourceEnum = ApplySourceEnum.PHONE;
        }
        // 按微信号搜索
        else if ((chatUser = this.chatUserService.queryOne(new ChatUser().setChatNo(param))) != null) {
            sourceEnum = ApplySourceEnum.CHAT_NO;
        }
        if (chatUser == null) {
            throw new BaseException("暂无结果");
        }
        if (ShiroUtils.getPhone().equals(chatUser.getPhone())) {
            throw new BaseException("不能添加自己为好友");
        }
        FriendVo07 friendVo = this.formatFriendVo(chatUser);
        if (friendVo.getSource() == null) {
            friendVo.setSource(sourceEnum);
        }
        return friendVo;
    }

    @Transactional
    @Override
    public void applyFriend(FriendVo02 friendVo) {
        //当前登录人
        Long userId = ShiroUtils.getUserId();
        Long friendId = friendVo.getUserId();
        // 验证是否是自己
        if (userId.equals(friendId)) {
            throw new BaseException("你不能添加自己为好友");
        }
        // 查询好友
        ChatUser user = this.chatUserService.getById(friendId);
        if (user == null) {
            throw new BaseException("好友不存在");
        }
        ChatFriend friend1 = this.getFriend(userId, friendId);
        ChatFriend friend2 = this.getFriend(friendId, userId);
        if (friend1 != null && friend2 != null) {
            throw new BaseException("已经是你的好友了，不能重复添加");
        }
        // 申请好友
        this.chatApplyService.applyFriend(friendId, friendVo.getSource(), friendVo.getReason());
    }

    @Transactional
    @Override
    public void agree(Long applyId) {
        ChatApply apply = this.verifyApply(applyId);
        ChatUser fromUser = this.chatUserService.getById(apply.getFromId());
        // 更新申请
        this.chatApplyService.updateById(new ChatApply()
                .setId(apply.getId())
                .setApplyStatus(ApplyStatusEnum.AGREE)
        );
        if (fromUser == null) {
            return;
        }
        if (ApplyTypeEnum.FRIEND.equals(apply.getApplyType())) {
            this.agreeFriend(apply, fromUser);
        } else {
            this.agreeGroup(apply, fromUser);
        }
    }

    /**
     * 同意朋友
     */
    private void agreeFriend(ChatApply apply, ChatUser fromUser) {
        Long toId = ShiroUtils.getUserId();
        Long fromId = apply.getFromId();
        Date now = DateUtil.date();
        ApplySourceEnum sourceEnum = apply.getApplySource();
        ChatUser toUser = this.chatUserService.getById(toId);
        // 添加好友列表
        List<ChatFriend> friendList = new ArrayList<>();
        ChatFriend friend1 = new ChatFriend()
                .setFromId(toId)
                .setToId(fromId);
        if (this.queryOne(friend1) == null) {
            friendList.add(friend1.setCreateTime(now)
                    .setSource(sourceEnum)
                    .setBlack(YesOrNoEnum.NO)
                    .setTop(YesOrNoEnum.NO)
                    .setRemark(fromUser.getNickName())
            );
        }
        ChatFriend friend2 = new ChatFriend()
                .setFromId(fromId)
                .setToId(toId);
        if (this.queryOne(friend2) == null) {
            friendList.add(friend2.setCreateTime(now)
                    .setSource(sourceEnum)
                    .setTop(YesOrNoEnum.NO)
                    .setBlack(YesOrNoEnum.NO)
                    .setRemark(toUser.getNickName()));
        }
        if (CollectionUtils.isEmpty(friendList)) {
            return;
        }
        // 增加好友数据
        this.batchAdd(friendList);
        // 发送通知
        this.chatPushService.pushMsg(ChatUser.initParam(fromUser).setContent(AppConstants.NOTICE_FRIEND_CREATE).setToId(toId), PushMsgEnum.ALERT);
        this.chatPushService.pushMsg(ChatUser.initParam(toUser).setContent(AppConstants.NOTICE_FRIEND_CREATE).setToId(fromId), PushMsgEnum.ALERT);
        
    }

    /**
     * 同意群组
     */
    private void agreeGroup(ChatApply apply, ChatUser fromUser) {
        Long toId = ShiroUtils.getUserId();
        Long fromId = apply.getFromId();
        Long groupId = apply.getTargetId();
        // 查询群
        ChatGroup group = this.groupService.getById(groupId);
        if (group == null) {
            return;
        }
        if (!toId.equals(group.getMaster())) {
            throw new BaseException("你不是群主，不能操作");
        }
        ChatGroupInfo groupInfo = this.groupInfoService.getGroupInfo(groupId, fromId, YesOrNoEnum.NO);
        // 加群
        if (groupInfo == null) {
            this.groupInfoService.add(new ChatGroupInfo(fromId, groupId));
        }
        // 发送通知
        String content = StrUtil.format(AppConstants.NOTICE_GROUP_JOIN, fromUser.getNickName());
        List<PushParamVo> pushParamList = this.groupService.queryGroupPushFrom(groupId, null, content);
        this.chatPushService.pushMsg(pushParamList, PushMsgEnum.ALERT);
    }

    @Override
    public void refused(Long applyId) {
        ChatApply apply = this.verifyApply(applyId);
        // 更新申请
        this.chatApplyService.updateById(new ChatApply().setId(apply.getId()).setApplyStatus(ApplyStatusEnum.REFUSED));
    }

    @Override
    public void ignore(Long applyId) {
        ChatApply apply = this.verifyApply(applyId);
        // 更新申请
        this.chatApplyService.updateById(new ChatApply().setId(apply.getId()).setApplyStatus(ApplyStatusEnum.IGNORE));
    }

    @Override
    public void setBlack(FriendVo03 friendVo) {
        Long userId = ShiroUtils.getUserId();
        Long friendId = friendVo.getUserId();
        // 检验是否是好友
        ChatFriend friend = this.getFriend(userId, friendId);
        if (friend == null) {
            throw new BaseException(AppConstants.FRIEND_NOT_EXIST);
        }
        this.updateById(new ChatFriend().setId(friend.getId()).setBlack(friendVo.getBlack()));
        // 移除缓存
        this.delFriendCache(userId, friendId);
    }

    @Transactional
    @Override
    public void delFriend(Long friendId) {
        Long userId = ShiroUtils.getUserId();
        // 校验是否是好友
        ChatFriend friend = this.getFriend(userId, friendId);
        if (friend == null) {
            throw new BaseException(AppConstants.FRIEND_NOT_EXIST);
        }
        this.deleteById(friend.getId());
        // 移除缓存
        this.delFriendCache(userId, friendId);
    }

    @Override
    public void setRemark(FriendVo05 friendVo) {
        Long userId = ShiroUtils.getUserId();
        Long friendId = friendVo.getUserId();
        // 校验是否是好友
        ChatFriend friend = getFriend(userId, friendId);
        if (friend == null) {
            throw new BaseException(AppConstants.FRIEND_NOT_EXIST);
        }
        ChatFriend chatFriend = new ChatFriend().setId(friend.getId()).setRemark(friendVo.getRemark());
        this.updateById(chatFriend);
        // 移除缓存
        this.delFriendCache(userId, friendId);
    }

    @Override
    public void setTop(FriendVo09 friendVo) {
        Long userId = ShiroUtils.getUserId();
        Long friendId = friendVo.getUserId();
        // 校验是否是好友
        ChatFriend friend = getFriend(userId, friendId);
        if (friend == null) {
            throw new BaseException(AppConstants.FRIEND_NOT_EXIST);
        }
        this.updateById(new ChatFriend().setId(friend.getId()).setTop(friendVo.getTop()));
        // 移除缓存
        this.delFriendCache(userId, friendId);
    }

    @Override
    public List<FriendVo06> friendList(String param) {
        List<FriendVo06> list1 = this.chatTalkService.queryFriendList();
        List<FriendVo06> list2 = this.chatFriendDao.friendList(ShiroUtils.getUserId());
        List<FriendVo06> dataList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list1)) {
            dataList.addAll(list1);
        }
        if (!CollectionUtils.isEmpty(list2)) {
            dataList.addAll(list2);
        }
        if (StringUtils.hasText(param)) {
            // 过滤
            dataList = dataList.stream().filter(data -> data.getNickName().contains(param)).collect(Collectors.toList());
        }
        return dataList;
    }

    /**
     * 格式化好友
     */
    private FriendVo07 formatFriendVo(ChatUser chatUser) {
        Long userId = ShiroUtils.getUserId();
        Long friendId = chatUser.getUserId();
        FriendVo07 friendVo = BeanUtil.toBean(chatUser, FriendVo07.class);
        // 校验是否是好友
        ChatFriend friend = this.getFriend(userId, friendId);
        if (friend == null) {
            return friendVo;
        }
        if (getFriend(friendId, userId) != null) {
            friendVo.setIsFriend(YesOrNoEnum.YES);
        }
        return friendVo.setBlack(friend.getBlack())
                .setNickName(friend.getRemark())
                .setSource(friend.getSource());
    }

    @Override
    public FriendVo07 getInfo(Long friendId) {
        Long userId = ShiroUtils.getUserId();
        FriendVo07 talk = this.chatTalkService.queryFriendInfo(friendId);
        if (talk != null) {
            return talk;
        }
        ChatUser chatUser = this.chatUserService.getById(friendId);
        if (chatUser == null) {
            throw new BaseException("用户信息不存在");
        }
        if (userId.equals(friendId)) {
            FriendVo07 friendVo = BeanUtil.toBean(chatUser, FriendVo07.class);
            return friendVo.setIsFriend(YesOrNoEnum.YES)
                    .setSource(ApplySourceEnum.SYS)
                    .setUserType(FriendTypeEnum.SELF);
        }
        return formatFriendVo(chatUser);
    }

    /**
     * 校验申请
     */
    private ChatApply verifyApply(Long applyId) {
        ChatApply apply = this.chatApplyService.getById(applyId);
        if (apply == null
                || !ShiroUtils.getUserId().equals(apply.getToId())
                || !ApplyStatusEnum.NONE.equals(apply.getApplyStatus())) {
            throw new BaseException("申请已过期，请刷新后重试");
        }
        return apply;
    }

    @Override
    public ChatFriend getFriend(Long userId, Long friendId) {
        String key = this.makeFriendKey(userId, friendId);
        if (this.redisUtils.hasKey(key)) {
            return JSONUtil.toBean(this.redisUtils.get(key), ChatFriend.class);
        }
        ChatFriend friend = this.queryOne(new ChatFriend().setFromId(userId).setToId(friendId));
        if (friend == null) {
            return null;
        }
        this.redisUtils.set(key, JSONUtil.toJsonStr(friend), AppConstants.REDIS_FRIEND_TIME, TimeUnit.DAYS);
        return friend;
    }

    @Override
    public List<Long> queryFriendId(Long userId) {
        return this.chatFriendDao.queryFriendId(userId);
    }

    /**
     * 生成好友缓存
     */
    private String makeFriendKey(Long userId, Long friendId) {
        return StrUtil.format(AppConstants.REDIS_FRIEND, userId, friendId);
    }

    /**
     * 删除好友缓存
     */
    private void delFriendCache(Long userId, Long friendId) {
        this.redisUtils.delete(this.makeFriendKey(userId, friendId));
    }

}
