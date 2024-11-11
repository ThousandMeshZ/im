package com.tmesh.im.app.chat.service.impl;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tmesh.im.app.chat.dao.ChatGroupInfoDao;
import com.tmesh.im.app.chat.domain.ChatGroupInfo;
import com.tmesh.im.app.chat.service.ChatGroupInfoService;
import com.tmesh.im.common.constant.AppConstants;
import com.tmesh.im.common.enums.YesOrNoEnum;
import com.tmesh.im.common.exception.BaseException;
import com.tmesh.im.common.redis.RedisUtils;
import com.tmesh.im.common.web.service.impl.BaseServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 群组信息 服务层实现
 */
@Service("chatGroupInfoService")
public class ChatGroupInfoServiceImpl extends BaseServiceImpl<ChatGroupInfo> implements ChatGroupInfoService {

    @Resource
    private ChatGroupInfoDao chatGroupInfoDao;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    public void setBaseDao() {
        super.setBaseDao(this.chatGroupInfoDao);
    }
    
    @Override
    public List<ChatGroupInfo> queryList(ChatGroupInfo chatGroupInfo) {
        return this.chatGroupInfoDao.queryList(chatGroupInfo);
    }
    
    @Override
    public ChatGroupInfo getGroupInfo(Long groupId, Long userId, YesOrNoEnum verify) {
        String key = StrUtil.format(AppConstants.REDIS_GROUP_INFO, groupId, userId);
        ChatGroupInfo chatGroupInfo;
        // 缓存存在
        if (this.redisUtils.hasKey(key)) {
            chatGroupInfo = JSONUtil.toBean(this.redisUtils.get(key), new TypeReference<ChatGroupInfo>() {}, false);
        }
        // 缓存不存在
        else if ((chatGroupInfo = this.queryOne(new ChatGroupInfo()
                .setUserId(userId)
                .setGroupId(groupId))) != null) {
            this.redisUtils.set(key, JSONUtil.toJsonStr(chatGroupInfo), AppConstants.REDIS_GROUP_TIME, TimeUnit.DAYS);
        }
        if (YesOrNoEnum.NO.equals(verify)) {
            return chatGroupInfo;
        }
        if (chatGroupInfo == null) {
            throw new BaseException("你不在当前群中");
        }
        return chatGroupInfo;
    }
    
    @Override
    public void delGroupInfoCache(Long groupId, List<Long> userList) {
        userList.forEach(elmemt -> 
                this.redisUtils.delete(StrUtil.format(AppConstants.REDIS_GROUP_INFO, groupId, elmemt)));
    }
    
    @Override
    public Long countByGroup(Long groupId) {
        return this.queryCount(new ChatGroupInfo()
                .setGroupId(groupId));
    }
    
    @Override
    public List<Long> queryUserList(Long groupId) {
        // 查询所有成员
        List<ChatGroupInfo> infoList = this.queryList(new ChatGroupInfo()
                .setGroupId(groupId));
        return infoList.stream().map(ChatGroupInfo::getUserId).toList();
    }
    
    @Override
    public List<ChatGroupInfo> queryUserList(Long groupId, List<Long> userList) {
        List<ChatGroupInfo> list = this.queryList(new ChatGroupInfo()
                .setGroupId(groupId));
        if (!CollectionUtils.isEmpty(userList)) {
            List<ChatGroupInfo> finalList = list;
            list = list.stream().filter(chatGroupInfo -> finalList.contains(chatGroupInfo.getUserId())).toList();
        }
        return list;
    }
    
    @Override
    public Map<Long, ChatGroupInfo> queryUserMap(Long groupId) {
        // 查询所有成员
        List<ChatGroupInfo> list = this.queryList(new ChatGroupInfo()
                .setGroupId(groupId));
        return list.stream().collect(
                Collectors.toMap(
                        ChatGroupInfo::getUserId, chatGroupInfo -> chatGroupInfo, (k1, k2) -> k1
                )
        );
    }

    @Override
    public void delByGroup(Long groupId) {
        this.chatGroupInfoDao.delete(new QueryWrapper<>(new ChatGroupInfo()
                .setGroupId(groupId)));
        // 删除群成员
        String redisKey = StrUtil.format(AppConstants.REDIS_GROUP_INFO, groupId, "*");
        this.redisUtils.delete(redisKey);
    }

}
