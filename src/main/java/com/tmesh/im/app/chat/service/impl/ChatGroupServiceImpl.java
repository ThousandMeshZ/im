package com.tmesh.im.app.chat.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.lang.PatternPool;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.github.pagehelper.PageHelper;
import com.tmesh.im.app.chat.dao.ChatGroupDao;
import com.tmesh.im.app.chat.domain.*;
import com.tmesh.im.app.chat.enums.ApplyStatusEnum;
import com.tmesh.im.app.chat.enums.ApplyTypeEnum;
import com.tmesh.im.app.chat.service.ChatApplyService;
import com.tmesh.im.app.chat.service.ChatGroupInfoService;
import com.tmesh.im.app.chat.service.ChatGroupService;
import com.tmesh.im.app.chat.service.ChatUserService;
import com.tmesh.im.app.chat.vo.GroupVo02;
import com.tmesh.im.app.chat.vo.GroupVo03;
import com.tmesh.im.app.chat.vo.GroupVo07;
import com.tmesh.im.app.chat.vo.GroupVo08;
import com.tmesh.im.app.push.enums.PushMsgEnum;
import com.tmesh.im.app.push.service.ChatPushService;
import com.tmesh.im.app.push.vo.PushParamVo;
import com.tmesh.im.common.constant.AppConstants;
import com.tmesh.im.common.enums.YesOrNoEnum;
import com.tmesh.im.common.exception.BaseException;
import com.tmesh.im.common.shiro.ShiroUtils;
import com.tmesh.im.common.web.service.impl.BaseServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 群组 服务层实现
 */
@Service("chatGroupService")
public class ChatGroupServiceImpl extends BaseServiceImpl<ChatGroup> implements ChatGroupService {

    @Resource
    private ChatGroupDao chatGroupDao;

    @Resource
    private ChatUserService chatUserService;

    @Resource
    private ChatGroupInfoService groupInfoService;

    @Resource
    private ChatApplyService chatApplyService;

    @Resource
    private ChatPushService chatPushService;

    @Value("${platform.group.portrait}")
    private String portrait;

    @Autowired
    public void setBaseDao() {
        super.setBaseDao(this.chatGroupDao);
    }

    @Override
    public List<ChatGroup> queryList(ChatGroup t) {
        return this.chatGroupDao.queryList(t);
    }

    @Transactional
    @Override
    public void createGroup(List<Long> list) {
        Long userId = ShiroUtils.getUserId();
        // 传入用户
        List<ChatUser> userList = this.verifyUserList(list);
        // 当前登陆人
        ChatUser master = this.chatUserService.getById(userId);
        // 当前时间
        Date now = DateUtil.date();
        // 建群
//        String portrait = "https://img.alicdn.com/imgextra/i3/87413133/O1CN01mHA9DJ1Z0xlORnKuW_!!87413133.png";
        String portrait = this.portrait;
        ChatGroup group = new ChatGroup()
                .setMaster(master.getUserId())
                .setName(StrUtil.format(AppConstants.GROUP_CREATE_NAME, master.getNickName(), RandomUtil.randomString(4)))
                .setPortrait(JSONUtil.toJsonStr(new JSONArray().put(portrait)))
                .setCreateTime(now);
        this.add(group);
        // 群明细
        List<ChatGroupInfo> groupInfoList = new ArrayList<>();
        userList.forEach(user -> 
                groupInfoList.add(new ChatGroupInfo(user.getUserId(), group.getId())
                        .setKeepGroup(YesOrNoEnum.YES)));

        groupInfoList.add(new ChatGroupInfo(master.getUserId(), group.getId())
                .setKeepGroup(YesOrNoEnum.YES));
        Integer batchAdd = this.groupInfoService.batchAdd(groupInfoList);
        String groupName = this.formatGroupName(group.getId(), group.getName());
        PushParamVo paramVo1 = new PushParamVo()
                .setUserId(group.getId())
                .setNickName(groupName)
                .setPortrait(group.getPortrait())
                .setContent(StrUtil.format(AppConstants.NOTICE_GROUP_CREATE_MEMBER, master.getNickName()));
        // 通知组员
        this.chatPushService.pushGroupMsg(this.formatFrom(list, paramVo1), paramVo1, PushMsgEnum.ALERT);
        // 通知群主
        List<String> nickList = userList.stream().map(ChatUser::getNickName).toList();
        String content = String.format(AppConstants.NOTICE_GROUP_CREATE_MEMBER, CollUtil.join(nickList, "、"));
        PushParamVo paramVo2 = new PushParamVo()
                .setUserId(group.getId())
                .setNickName(groupName)
                .setPortrait(group.getPortrait())
                .setContent(content)
                .setToId(userId);
        this.chatPushService.pushGroupMsg(Arrays.asList(paramVo2), paramVo2, PushMsgEnum.ALERT);
    }

    /**
     * 计算群名
     */
    private String formatGroupName(Long groupId, String groupName) {
        if (!StringUtils.hasText(groupName)) {
            groupName = this.getById(groupId).getName();
        }
        Long count = this.groupInfoService.countByGroup(groupId);
        return StrUtil.format("{}({})", groupName, count);
    }
    
    @Override
    public Dict getInfo(Long groupId) {
        // 校验
        ChatGroup group = this.getById(groupId);
        if (group == null) {
            throw new BaseException("当前群不存在");
        }
        Long userId = ShiroUtils.getUserId();
        ChatGroupInfo groupInfo = this.groupInfoService.getGroupInfo(groupId, userId, YesOrNoEnum.YES);
        // 组装返回值
        Dict groupDict = Dict.create().parseBean(group)
                .filter("name", "notice", "portrait")
                .set("groupId", group.getId());
        Dict set = Dict.create().parseBean(groupInfo)
                .filter("top", "disturb", "keepGroup");
        List<Long> userList = this.groupInfoService.queryUserList(groupId);
        List<Dict> userDictList = this.chatUserService.getByIds(userList)
                .stream().collect(ArrayList::new, (x, y) -> {
                    x.add(Dict.create().parseBean(y).filter("userId", "nickName", "portrait"));
                }, ArrayList::addAll);
        return Dict.create()
                .set("user", userDictList)
                .set("group", groupDict)
                .set("set", set)
                .set("master", group.getMaster().equals(userId) ? YesOrNoEnum.YES : YesOrNoEnum.NO);
    }

    @Transactional
    @Override
    public void invitationGroup(Long groupId, List<Long> list) {
        // 查询用户列表
        List<ChatUser> userList = this.verifyUserList(list);
        // 当前用户
        Long userId = ShiroUtils.getUserId();
        // 验证群
        this.groupInfoService.getGroupInfo(groupId, userId, YesOrNoEnum.YES);
        // 集合
        Map<Long, ChatGroupInfo> infoMap = this.groupInfoService.queryUserMap(groupId);
        // 群明细
        List<ChatGroupInfo> infoList = new ArrayList<>();
        List<ChatUser> newUserList = new ArrayList<>();
        userList.forEach(chatUser -> {
            ChatGroupInfo groupInfo = infoMap.get(chatUser.getUserId());
            // 新增
            if (groupInfo == null) {
                infoList.add(new ChatGroupInfo(chatUser.getUserId(), groupId));
                newUserList.add(chatUser);
            }
        });
        // 批量信息
        this.groupInfoService.batchAdd(infoList);
        // 通知
        ChatGroup group = this.getById(groupId);
        List<String> nickList = newUserList.stream().map(ChatUser::getNickName).toList();
        String content = StrUtil.format(AppConstants.NOTICE_GROUP_JOIN, CollUtil.join(nickList, "、"));
        List<PushParamVo> pushParamList = this.queryPushParam(group, content);
        this.chatPushService.pushGroupMsg(pushParamList, this.initGroupParam(group), PushMsgEnum.ALERT);
    }

    /**
     * 更新群主
     */
    @Transactional
    protected void updMaster(ChatGroup group) {
        Long groupId = group.getId();
        // 执行分页
        PageHelper.startPage(1, 1, "info_id");
        // 查询所有成员
        List<Long> userList = groupInfoService.queryUserList(groupId);
        if (CollectionUtils.isEmpty(userList)) {
            // 删除群
            this.deleteById(groupId);
            return;
        }
        Long masterId = userList.get(0);
        // 更新群主
        this.updateById(new ChatGroup().setId(groupId).setMaster(masterId));
        this.groupInfoService.updateById(new ChatGroupInfo().setInfoId(masterId).setKeepGroup(YesOrNoEnum.YES));
        // 更新申请人
        ChatApply query = new ChatApply().setTargetId(groupId).setApplyType(ApplyTypeEnum.GROUP).setApplyStatus(ApplyStatusEnum.NONE);
        this.chatApplyService.queryList(query).forEach(chatApply ->
            this.chatApplyService.updateById(new ChatApply().setId(chatApply.getId()).setToId(masterId))
        );
        ChatGroupInfo groupInfo = this.groupInfoService.getGroupInfo(groupId, masterId, YesOrNoEnum.NO);
        String groupName = formatGroupName(group.getId(), group.getName());
        // 通知新群主
        PushParamVo pushParamVo = new PushParamVo()
                .setUserId(group.getId())
                .setNickName(groupName)
                .setPortrait(group.getPortrait())
                .setDisturb(groupInfo.getDisturb())
                .setTop(groupInfo.getTop())
                .setContent(AppConstants.NOTICE_GROUP_TRANSFER);
        this.chatPushService.pushGroupMsg(pushParamVo.setToId(masterId), this.initGroupParam(group), PushMsgEnum.ALERT);
    }

    /**
     * 验证人员信息
     */
    private List<ChatUser> verifyUserList(List<Long> list) {
        // 验证
        if (CollectionUtils.isEmpty(list)) {
            throw new BaseException("好友列表不能为空");
        }
        if (list.contains(ShiroUtils.getUserId())) {
            throw new BaseException("好友列表不能包含自己");
        }
        // 去重
        list = list.stream().distinct().collect(Collectors.toList());
        // 验证
        if (CollectionUtils.isEmpty(list)) {
            throw new BaseException("好友列表不能为空");
        }
        // 查询用户列表
        return chatUserService.getByIds(list);
    }

    @Transactional
    @Override
    public void kickedGroup(Long groupId, List<Long> list) {
        // 查询用户列表
        List<ChatUser> userList = verifyUserList(list);
        // 当前用户
        Long userId = ShiroUtils.getUserId();
        // 验证群
        ChatGroupInfo groupInfo = this.groupInfoService.getGroupInfo(groupId, userId, YesOrNoEnum.YES);
        // 查询群
        ChatGroup group = this.getById(groupId);
        if (!userId.equals(group.getMaster())) {
            throw new BaseException("你不是群主，不能操作");
        }
        // 删除成员
        this.groupInfoService.queryUserList(groupId, list).forEach(e ->
            this.groupInfoService.deleteById(e.getInfoId())
        );
        String groupName = this.formatGroupName(group.getId(), group.getName());
        // 群主
        List<String> nickList = userList.stream().map(ChatUser::getNickName).collect(Collectors.toList());
        PushParamVo pushParamVo = new PushParamVo()
                .setUserId(group.getId())
                .setNickName(groupName)
                .setPortrait(group.getPortrait())
                .setDisturb(groupInfo.getDisturb())
                .setTop(groupInfo.getTop())
                .setContent(StrUtil.format(AppConstants.NOTICE_GROUP_KICKED_MASTER, CollUtil.join(nickList, "、")));
        this.chatPushService.pushGroupMsg(pushParamVo.setToId(group.getMaster()), pushParamVo, PushMsgEnum.ALERT);
        // 组员
        List<PushParamVo> paramList = queryGroupPushFrom(groupId, list, AppConstants.NOTICE_GROUP_KICKED_MEMBER);
        this.chatPushService.pushGroupMsg(paramList, pushParamVo, PushMsgEnum.ALERT);
        // 删除缓存
        this.groupInfoService.delGroupInfoCache(groupId, list);
    }

    @Override
    public String getGroupQrCode(Long groupId) {
        return AppConstants.QR_CODE_GROUP + groupId;
    }

    @Override
    public void editTop(Long groupId, YesOrNoEnum top) {
        Long userId = ShiroUtils.getUserId();
        ChatGroupInfo info = this.groupInfoService.getGroupInfo(groupId, userId, YesOrNoEnum.YES);
        this.groupInfoService.updateById(new ChatGroupInfo().setInfoId(info.getInfoId()).setTop(top));
        // 删除缓存
        this.groupInfoService.delGroupInfoCache(groupId, Arrays.asList(userId));
    }

    @Override
    public void editDisturb(Long groupId, YesOrNoEnum disturb) {
        Long userId = ShiroUtils.getUserId();
        ChatGroupInfo info = this.groupInfoService.getGroupInfo(groupId, userId, YesOrNoEnum.YES);
        this.groupInfoService.updateById(new ChatGroupInfo().setInfoId(info.getInfoId()).setDisturb(disturb));
        // 删除缓存
        this.groupInfoService.delGroupInfoCache(groupId, Arrays.asList(userId));
    }

    @Override
    public void editKeepGroup(Long groupId, YesOrNoEnum keepGroup) {
        Long userId = ShiroUtils.getUserId();
        ChatGroupInfo info = this.groupInfoService.getGroupInfo(groupId, userId, YesOrNoEnum.YES);
        this.groupInfoService.updateById(new ChatGroupInfo().setInfoId(info.getInfoId()).setKeepGroup(keepGroup));
        // 删除缓存
        this.groupInfoService.delGroupInfoCache(groupId, Arrays.asList(userId));
    }

    @Transactional
    @Override
    public void logoutGroup(Long groupId) {
        Long userId = ShiroUtils.getUserId();
        ChatUser chatUser = this.chatUserService.getById(userId);
        ChatGroupInfo groupInfo = this.groupInfoService.getGroupInfo(groupId, userId, YesOrNoEnum.YES);
        ChatGroup group = this.getById(groupId);
        // 删除数据
        this.groupInfoService.deleteById(groupInfo.getInfoId());
        // 删除缓存
        this.groupInfoService.delGroupInfoCache(groupId, Collections.singletonList(userId));
        if (userId.equals(group.getMaster())) {
            // 变更群主
            ThreadUtil.execAsync(() -> this.updMaster(group));
        } else {
            String groupName = this.formatGroupName(groupId, group.getName());
            // 通知
            PushParamVo pushParamVo = new PushParamVo()
                    .setUserId(group.getId())
                    .setPortrait(group.getPortrait())
                    .setNickName(groupName)
                    .setDisturb(groupInfo.getDisturb())
                    .setTop(groupInfo.getTop())
                    .setContent(StrUtil.format(AppConstants.NOTICE_GROUP_LOGOUT, chatUser.getNickName()))
                    .setToId(group.getMaster());
            this.chatPushService.pushGroupMsg(pushParamVo, pushParamVo, PushMsgEnum.ALERT);
        }
    }

    @Transactional
    @Override
    public void removeGroup(Long groupId) {
        Long userId = ShiroUtils.getUserId();
        ChatGroup group = this.getById(groupId);
        if (group == null) {
            throw new BaseException("当前群组不存在");
        }
        if (!userId.equals(group.getMaster())) {
            throw new BaseException("你不是群主，不能操作");
        }
        List<PushParamVo> userList = this.queryPushParam(group, AppConstants.NOTICE_GROUP_DISSOLVE);
        this.deleteById(groupId);
        // 删除数据
        this.groupInfoService.delByGroup(groupId);
        // 通知
        this.chatPushService.pushGroupMsg(userList, this.initGroupParam(group), PushMsgEnum.ALERT);
    }

    private List<PushParamVo> queryPushParam(ChatGroup group, String content) {
        List<ChatGroupInfo> dataList = this.groupInfoService.queryList(new ChatGroupInfo().setGroupId(group.getId()));
        String groupName = this.formatGroupName(group.getId(), group.getName());
        List<PushParamVo> list = new ArrayList<>();
        dataList.forEach(groupInfo -> list.add(
                new PushParamVo()
                        .setUserId(group.getId())
                        .setNickName(groupName)
                        .setPortrait(group.getPortrait())
                        .setToId(groupInfo.getUserId())
                        .setDisturb(groupInfo.getDisturb())
                        .setContent(content)
        ));
        return list;
    }

    @Override
    public GroupVo07 scanCode(String param) {
        // 校验前缀
        if (!StrUtil.startWith(param, AppConstants.QR_CODE_GROUP)) {
            throw new BaseException("参数错误");
        }
        Long groupId = Convert.toLong(ReUtil.get(PatternPool.NUMBERS, param, 0), null);
        ChatGroup group = this.getById(groupId);
        if (group == null) {
            throw new BaseException("当前群组不存在");
        }
        return BeanUtil.toBean(group, GroupVo07.class)
                .setPortrait(JSONUtil.toList(group.getPortrait(), String.class))
                .setCount(this.groupInfoService.countByGroup(groupId));
    }

    @Override
    public void joinGroup(Long groupId) {
        Long userId = ShiroUtils.getUserId();
        ChatGroupInfo groupInfo = this.groupInfoService.getGroupInfo(groupId, userId, YesOrNoEnum.NO);
        ChatGroup group = this.getById(groupId);
        // 加群
        if (groupInfo == null) {
            this.groupInfoService.add(new ChatGroupInfo(userId, groupId));
            // 全员
            // 查询当前登录用户
            ChatUser chatUser = this.chatUserService.getById(userId);
            String content = StrUtil.format(AppConstants.NOTICE_GROUP_JOIN, chatUser.getNickName());
            List<PushParamVo> userList = this.queryPushParam(group, content);
            // 通知
            this.chatPushService.pushGroupMsg(userList, this.initGroupParam(group), PushMsgEnum.ALERT);
        }
    }

    /**
     * 组装群对象
     */
    private PushParamVo initGroupParam(ChatGroup group) {
        return new PushParamVo()
                .setUserId(group.getId())
                .setNickName(group.getName())
                .setPortrait(group.getPortrait());
    }

    @Override
    public List<GroupVo08> groupList() {
        // 结果
        List<GroupVo08> dataList = new ArrayList<>();
        // 查询明细
        List<Long> groupList = this.groupInfoService.queryList(
                new ChatGroupInfo().setUserId(ShiroUtils.getUserId())
        ).stream().map(ChatGroupInfo::getGroupId).toList();
        // 集合判空
        if (CollectionUtils.isEmpty(groupList)) {
            return dataList;
        }
        // 查询群组
        this.getByIds(groupList).forEach(group ->
                dataList.add(
                        BeanUtil.toBean(group, GroupVo08.class)
                                .setPortrait(JSONUtil.toList(group.getPortrait(), String.class))
                                .setGroupId(group.getId())
                ));
        return dataList;
    }

    @Override
    public List<PushParamVo> queryFriendPushFrom(ChatMsg chatMsg) {
        Long userId = ShiroUtils.getUserId();
        List<PushParamVo> paramList = this.chatGroupDao.queryFriendPushFrom(chatMsg.getToId(), userId);
        ChatUser fromUser = this.chatUserService.getById(userId);
        paramList.forEach(param -> {
            param.setUserId(fromUser.getUserId());
            if (!StringUtils.hasText(param.getNickName())) {
                param.setNickName(fromUser.getNickName());
            }
            param.setPortrait(fromUser.getPortrait());
            param.setContent(chatMsg.getContent());
            param.setMsgId(chatMsg.getId());
        });
        return paramList;
    }

    @Override
    public List<PushParamVo> queryGroupPushFrom(Long groupId, List<Long> list, String content) {
        // 查询群
        ChatGroup group = new ChatGroup();
        String groupName = this.formatGroupName(groupId, group.getName());
        // 成员
        List<ChatGroupInfo> groupInfoList = this.groupInfoService.queryUserList(groupId, list);

       return groupInfoList.stream().map(groupInfo ->
               new PushParamVo()
                    .setUserId(group.getId())
                    .setNickName(groupName)
                    .setPortrait(group.getPortrait())
                    .setToId(groupInfo.getUserId())
                    .setDisturb(groupInfo.getDisturb())
                    .setTop(groupInfo.getTop())
                    .setContent(AppConstants.NOTICE_GROUP_KICKED_MEMBER)
        ).toList();

        /* List<PushParamVo> paramList = new ArrayList<>();
        groupInfoList.forEach(e -> {
            PushParamVo paramVo = new PushParamVo()
                    .setUserId(group.getId())
                    .setNickName(groupName)
                    .setPortrait(group.getPortrait())
                    .setToId(e.getUserId())
                    .setDisturb(e.getDisturb())
                    .setTop(e.getTop())
                    .setContent(AppConstants.NOTICE_GROUP_KICKED_MEMBER);
            paramList.add(paramVo);
        });
        return paramList; */
    }

    @Override
    public void editGroupName(GroupVo02 groupVo) {
        Long userId = ShiroUtils.getUserId();
        Long groupId = groupVo.getGroupId();
        ChatUser chatUser = this.chatUserService.getById(userId);
        this.groupInfoService.getGroupInfo(groupId, userId, YesOrNoEnum.YES);
        this.updateById(new ChatGroup().setId(groupId).setName(groupVo.getName()));
        String groupName = this.formatGroupName(groupId, groupVo.getName());
        PushParamVo paramVo = new PushParamVo()
                .setUserId(groupId)
                .setNickName(groupName)
                .setPortrait(this.getById(groupId).getPortrait())
                .setContent(StrUtil.format(AppConstants.NOTICE_GROUP_EDIT, chatUser.getNickName(), groupVo.getName()));
        // 通知组员
        List<Long> userList = this.groupInfoService.queryUserList(groupId);
        this.chatPushService.pushGroupMsg(this.formatFrom(userList, paramVo), paramVo, PushMsgEnum.ALERT);
    }

    @Override
    public void editGroupNotice(GroupVo03 groupVo) {
        Long userId = ShiroUtils.getUserId();
        Long groupId = groupVo.getGroupId();
        this.groupInfoService.getGroupInfo(groupId, userId, YesOrNoEnum.YES);
        ChatGroup group = new ChatGroup()
                .setId(groupVo.getGroupId())
                .setNotice(groupVo.getNotice());
        this.updateById(group);
        String groupName = this.formatGroupName(groupId, null);
        ChatUser chatUser = this.chatUserService.getById(userId);
        PushParamVo paramVo = new PushParamVo()
                .setUserId(groupId)
                .setNickName(groupName)
                .setPortrait(this.getById(groupId).getPortrait())
                .setContent(StrUtil.format(AppConstants.NOTICE_GROUP_NOTICE, chatUser.getNickName(), group.getNotice()));
        // 通知组员
        List<Long> userList = this.groupInfoService.queryUserList(userId);
        this.chatPushService.pushGroupMsg(this.formatFrom(userList, paramVo), paramVo, PushMsgEnum.ALERT);
    }

    private List<PushParamVo> formatFrom(List<Long> userList, PushParamVo paramVo) {
        List<PushParamVo> paramList = new ArrayList<>();
        userList.forEach(e -> {
            paramList.add(BeanUtil.toBean(paramVo, PushParamVo.class).setToId(e));
        });
        return paramList;
    }
}
