package com.tmesh.im.app.chat.controller;

import com.tmesh.im.app.chat.service.ChatGroupService;
import com.tmesh.im.app.chat.service.ChatMsgService;
import com.tmesh.im.app.chat.vo.*;
import com.tmesh.im.common.version.ApiVersion;
import com.tmesh.im.common.version.VersionEnum;
import com.tmesh.im.common.web.controller.BaseController;
import com.tmesh.im.common.web.domain.AjaxResult;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 群组控制器
 */
@RestController
@Slf4j
@RequestMapping("/group")
public class GroupController extends BaseController {

    @Resource
    private ChatGroupService chatGroupService;

    @Resource
    private ChatMsgService chatMsgService;

    /**
     * 建立群组
     */
    @ApiVersion(VersionEnum.V1_0_0)
    @PostMapping("/createGroup")
    public AjaxResult createGroup(@Validated @RequestBody List<Long> list) {
        this.chatGroupService.createGroup(list);
        return AjaxResult.success();
    }

    /**
     * 获取群详情
     */
    @ApiVersion(VersionEnum.V1_0_0)
    @GetMapping("/getInfo/{groupId}")
    public AjaxResult getInfo(@PathVariable Long groupId) {
        return AjaxResult.success(this.chatGroupService.getInfo(groupId));
    }

    /**
     * 邀请进群
     */
    @ApiVersion(VersionEnum.V1_0_0)
    @PostMapping("/invitationGroup")
    public AjaxResult invitationGroup(@Validated @RequestBody GroupVo01 groupVo) {
        Long groupId = groupVo.getGroupId();
        List<Long> list = groupVo.getList();
        this.chatGroupService.invitationGroup(groupId, list);
        return AjaxResult.success();
    }

    /**
     * 踢出群组
     */
    @ApiVersion(VersionEnum.V1_0_0)
    @PostMapping("/kickedGroup")
    public AjaxResult kickedGroup(@Validated @RequestBody GroupVo01 groupVo) {
        Long groupId = groupVo.getGroupId();
        List<Long> list = groupVo.getList();
        this.chatGroupService.kickedGroup(groupId, list);
        return AjaxResult.success();
    }

    /**
     * 修改群名
     */
    @ApiVersion(VersionEnum.V1_0_0)
    @PostMapping("/editGroupName")
    public AjaxResult editGroupName(@Validated @RequestBody GroupVo02 groupVo) {
        this.chatGroupService.editGroupName(groupVo);
        return AjaxResult.success();
    }

    /**
     * 获取群组二维码
     */
    @ApiVersion(VersionEnum.V1_0_0)
    @GetMapping("/getGroupQrCode/{groupId}")
    public AjaxResult getGroupQrCode(@PathVariable Long groupId) {
        return AjaxResult.success(this.chatGroupService.getGroupQrCode(groupId));
    }

    /**
     * 修改群公告
     */
    @ApiVersion(VersionEnum.V1_0_0)
    @PostMapping("/editGroupNotice")
    public AjaxResult editGroupNotice(@Validated @RequestBody GroupVo03 groupVo) {
        this.chatGroupService.editGroupNotice(groupVo);
        return AjaxResult.success();
    }

    /**
     * 是否置顶
     */
    @ApiVersion(VersionEnum.V1_0_0)
    @PostMapping("/editTop")
    public AjaxResult editTop(@Validated @RequestBody GroupVo04 groupVo) {
        Long groupId = groupVo.getGroupId();
        this.chatGroupService.editTop(groupId, groupVo.getTop());
        return AjaxResult.success();
    }

    /**
     * 是否免打扰
     */
    @ApiVersion(VersionEnum.V1_0_0)
    @PostMapping("/editDisturb")
    public AjaxResult editDisturb(@Validated @RequestBody GroupVo05 groupVo) {
        Long groupId = groupVo.getGroupId();
        this.chatGroupService.editDisturb(groupId, groupVo.getDisturb());
        return AjaxResult.success();
    }

    /**
     * 是否保存群组
     */
    @ApiVersion(VersionEnum.V1_0_0)
    @PostMapping("/editKeepGroup")
    public AjaxResult editKeepGroup(@Validated @RequestBody GroupVo06 groupVo) {
        Long groupId = groupVo.getGroupId();
        this.chatGroupService.editKeepGroup(groupId, groupVo.getKeepGroup());
        return AjaxResult.success();
    }

    /**
     * 退出群组
     */
    @ApiVersion(VersionEnum.V1_0_0)
    @GetMapping("/logoutGroup/{groupId}")
    public AjaxResult logoutGroup(@PathVariable Long groupId) {
        this.chatGroupService.logoutGroup(groupId);
        return AjaxResult.success();
    }

    /**
     * 解散群组
     */
    @ApiVersion(VersionEnum.V1_0_0)
    @GetMapping("/removeGroup/{groupId}")
    public AjaxResult removeGroup(@PathVariable Long groupId) {
        this.chatGroupService.removeGroup(groupId);
        return AjaxResult.success();
    }

    /**
     * 扫码查询
     */
    @ApiVersion(VersionEnum.V1_0_0)
    @GetMapping("/scanCode/{param}")
    public AjaxResult scanCode(@PathVariable String param) {
        return AjaxResult.success(this.chatGroupService.scanCode(param));
    }

    /**
     * 加入群组
     */
    @ApiVersion(VersionEnum.V1_0_0)
    @GetMapping("/joinGroup/{groupId}")
    public AjaxResult joinGroup(@PathVariable Long groupId) {
        this.chatGroupService.joinGroup(groupId);
        return AjaxResult.success();
    }

    /**
     * 查询群列表
     */
    @ApiVersion(VersionEnum.V1_0_0)
    @GetMapping("/groupList")
    public AjaxResult groupList() {
        return AjaxResult.success(this.chatGroupService.groupList());
    }

    /**
     * 发送信息
     */
    @ApiVersion(VersionEnum.V1_0_0)
    @PostMapping("/sendMsg")
    public AjaxResult sendMsg(@Validated @RequestBody ChatVo02 chatVo) {
        return AjaxResult.success(this.chatMsgService.sendGroupMsg(chatVo));
    }

}
