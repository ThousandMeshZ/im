package com.tmesh.im.app.chat.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.github.pagehelper.PageInfo;
import com.tmesh.im.app.chat.dao.ChatApplyDao;
import com.tmesh.im.app.chat.domain.ChatApply;
import com.tmesh.im.app.chat.domain.ChatUser;
import com.tmesh.im.app.chat.enums.ApplySourceEnum;
import com.tmesh.im.app.chat.enums.ApplyStatusEnum;
import com.tmesh.im.app.chat.enums.ApplyTypeEnum;
import com.tmesh.im.app.chat.service.ChatApplyService;
import com.tmesh.im.app.chat.service.ChatUserService;
import com.tmesh.im.app.chat.vo.ApplyVo02;
import com.tmesh.im.app.chat.vo.ApplyVo03;
import com.tmesh.im.app.push.enums.PushNoticeEnum;
import com.tmesh.im.app.push.service.ChatPushService;
import com.tmesh.im.app.push.vo.PushParamVo;
import com.tmesh.im.common.constant.AppConstants;
import com.tmesh.im.common.exception.BaseException;
import com.tmesh.im.common.redis.RedisUtils;
import com.tmesh.im.common.shiro.ShiroUtils;
import com.tmesh.im.common.web.service.impl.BaseServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 好友申请 服务层实现
 */
@Service("chatApplyService")
public class ChatApplyServiceImpl extends BaseServiceImpl<ChatApply> implements ChatApplyService {

    @Resource
    private ChatApplyDao chatApplyDao;

    @Lazy
    @Resource
    private ChatUserService chatUserService;

    @Resource
    private ChatPushService chatPushService;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    public void setBaseDao() {
        super.setBaseDao(this.chatApplyDao);
    }

    @Override
    public List<ChatApply> queryList(ChatApply t) {
        List<ChatApply> dataList = chatApplyDao.queryList(t);
        return dataList;
    }
    
    @Override
    public void applyFriend(Long acceptId, ApplySourceEnum applySource, String reason) {
        Date now = DateUtil.date();
        Long fromId = ShiroUtils.getUserId();
        // 查询
        ChatApply queryChatApply = new ChatApply()
                .setFromId(fromId)
                .setToId(acceptId)
                .setTargetId(acceptId)
                .setApplyType(ApplyTypeEnum.FRIEND)
                .setApplyStatus(ApplyStatusEnum.NONE);
        
        ChatApply chatApply = this.queryOne(queryChatApply);
        queryChatApply.setApplySource(applySource)
                .setReason(reason)
                .setCreateTime(now);
        if (chatApply == null) {
            this.add(chatApply);
        } else {
            this.updateById(queryChatApply.setId(chatApply.getId()));
        }

        // 给好友发送通知
        PushParamVo paramVo = new PushParamVo().setToId(acceptId);
        this.chatPushService.pushNotice(paramVo, PushNoticeEnum.FRIEND_APPLY);
    }
    
    @Override
    public PageInfo list() {
        Long userId = ShiroUtils.getUserId();
        // 清空角标
        this.redisUtils.delete(AppConstants.REDIS_FRIEND_NOTICE + userId);
        // 查询
        List<ChatApply> dataList = this.queryList(new ChatApply().setToId(userId));
        // 获取申请人
        List<Long> fromList = dataList.stream().map(ChatApply::getFromId).toList();
        // 集合判空
        if (CollectionUtils.isEmpty(fromList)) {
            return new PageInfo<>();
        }
        // 查询申请人
        Map<Long, ChatUser> dataMap = this.chatUserService.getByIds(fromList)
                .stream().collect(HashMap::new, (x, y) -> x.put(y.getUserId(), y)
                        , HashMap::putAll);
        // 转换
        List<ApplyVo02> dictList = new ArrayList<>();
        for (ChatApply apply : dataList) {
            ChatUser chatUser = ChatUser.initUser(dataMap.get(apply.getFromId()));
            ApplyVo02 applyVo = BeanUtil.toBean(apply, ApplyVo02.class)
                    .setApplyId(apply.getId())
                    .setUserId(apply.getFromId())
                    .setPortrait(chatUser.getPortrait())
                    .setNickName(chatUser.getNickName());
            dictList.add(applyVo);
        }
        return this.getPageInfo(dictList, dataList);
    }
    
    @Override
    public ApplyVo03 getInfo(Long applyId) {
        ChatApply apply = this.getById(applyId);
        if (apply == null) {
            throw new BaseException("申请已过期，请刷新后重试");
        }
        ChatUser chatUser = ChatUser.initUser(this.chatUserService.getById(apply.getFromId()));
        return BeanUtil.toBean(chatUser, ApplyVo03.class)
                .setApplyId(applyId)
                .setApplySource(apply.getApplySource())
                .setApplyStatus(apply.getApplyStatus())
                .setReason(apply.getReason());
    }
}
