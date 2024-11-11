package com.tmesh.im.app.chat.controller;

import com.tmesh.im.app.chat.service.ChatFriendService;
import com.tmesh.im.app.chat.vo.*;
import com.tmesh.im.common.version.ApiVersion;
import com.tmesh.im.common.version.VersionEnum;
import com.tmesh.im.common.web.controller.BaseController;
import com.tmesh.im.common.web.domain.AjaxResult;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 好友控制器
 */
@RestController
@Slf4j
@RequestMapping("/friend")
public class FriendController extends BaseController {

    @Resource
    private ChatFriendService chatFriendService;

    /**
     * 搜索好友
     */
    @ApiVersion(VersionEnum.V1_0_0)
    @PostMapping("/findFriend")
    public AjaxResult findFriend(@RequestBody @Validated FriendVo01 friendVo) {
        return AjaxResult.success(this.chatFriendService.findFriend(friendVo.getParam()));
    }

    /**
     * 好友列表
     */
    @ApiVersion(VersionEnum.V1_0_0)
    @PostMapping("/friendList")
    public AjaxResult friendList(@RequestBody @Validated FriendVo08 friendVo) {
        return AjaxResult.success(this.chatFriendService.friendList(friendVo.getParam()));
    }

    /**
     * 好友详情
     */
    @ApiVersion(VersionEnum.V1_0_0)
    @GetMapping("/info/{friendId}")
    public AjaxResult getInfo(@PathVariable Long friendId) {
        return AjaxResult.success(this.chatFriendService.getInfo(friendId));
    }

    /**
     * 设置黑名单
     */
    @ApiVersion(VersionEnum.V1_0_0)
    @PostMapping("/black")
    public AjaxResult black(@RequestBody @Validated FriendVo03 friendVo) {
        this.chatFriendService.setBlack(friendVo);
        return AjaxResult.success();
    }

    /**
     * 删除好友
     */
    @ApiVersion(VersionEnum.V1_0_0)
    @PostMapping("/delFriend")
    public AjaxResult delFriend(@RequestBody @Validated FriendVo04 friendVo) {
        this.chatFriendService.delFriend(friendVo.getUserId());
        return AjaxResult.success();
    }

    /**
     * 设置备注
     */
    @ApiVersion(VersionEnum.V1_0_0)
    @PostMapping("/remark")
    public AjaxResult remark(@Validated @RequestBody FriendVo05 friendVo) {
        this.chatFriendService.setRemark(friendVo);
        return AjaxResult.success();
    }

    /**
     * 设置是否置顶
     */
    @ApiVersion(VersionEnum.V1_0_0)
    @PostMapping("/top")
    public AjaxResult top(@Validated @RequestBody FriendVo09 friendVo) {
        this.chatFriendService.setTop(friendVo);
        return AjaxResult.success();
    }
}
