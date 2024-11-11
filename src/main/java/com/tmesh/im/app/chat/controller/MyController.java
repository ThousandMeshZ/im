package com.tmesh.im.app.chat.controller;

import com.tmesh.im.app.chat.domain.ChatUser;
import com.tmesh.im.app.chat.service.ChatFeedbackService;
import com.tmesh.im.app.chat.service.ChatUserService;
import com.tmesh.im.app.chat.vo.*;
import com.tmesh.im.common.shiro.ShiroUtils;
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
 * @description : 我的
 */
@RestController
@Slf4j
@RequestMapping("/my")
public class MyController extends BaseController {

    @Resource
    private ChatUserService chatUserService;

    @Resource
    private ChatFeedbackService feedbackService;

    /**
     * 修改密码
     */
    @ApiVersion(VersionEnum.V1_0_0)
    @PostMapping("/editPass")
    public AjaxResult editPass(@Validated @RequestBody MyVo01 myVo) {
        // 执行重置
        this.chatUserService.editPass(myVo.getPassword(), myVo.getPwd());
        return AjaxResult.success("修改成功");
    }

    /**
     * 退出系统
     */
    @ApiVersion(VersionEnum.V1_0_0)
    @GetMapping("/logout")
    public AjaxResult logout() {
        this.chatUserService.logout();
        return AjaxResult.success();
    }

    /**
     * 获取二维码
     */
    @ApiVersion(VersionEnum.V1_0_0)
    @GetMapping("/getQrCode")
    public AjaxResult getQrCode() {
        return AjaxResult.success(this.chatUserService.getQrCode());
    }

    /**
     * 获取基本信息
     */
    @ApiVersion(VersionEnum.V1_0_0)
    @GetMapping("/getInfo")
    public AjaxResult getInfo() {
        return AjaxResult.success(this.chatUserService.getInfo());
    }

    /**
     * 修改头像
     */
    @ApiVersion(VersionEnum.V1_0_0)
    @PostMapping("/editPortrait")
    public AjaxResult editPortrait(@Validated @RequestBody MyVo02 myVo) {
        // 执行修改
        ChatUser chatUser = new ChatUser()
                .setUserId(ShiroUtils.getUserId())
                .setPortrait(myVo.getPortrait());
        this.chatUserService.updateById(chatUser);
        return AjaxResult.success("修改成功");
    }

    /**
     * 修改昵称
     */
    @ApiVersion(VersionEnum.V1_0_0)
    @PostMapping("/editNick")
    public AjaxResult editNick(@Validated @RequestBody MyVo03 myVo) {
        ChatUser chatUser = new ChatUser()
                .setUserId(ShiroUtils.getUserId())
                .setNickName(myVo.getNickName());
        this.chatUserService.updateById(chatUser);
        return AjaxResult.success("修改成功");
    }

    /**
     * 修改性别
     */
    @ApiVersion(VersionEnum.V1_0_0)
    @PostMapping("/editGender")
    public AjaxResult editGender(@Validated @RequestBody MyVo05 myVo) {
        // 执行修改
        ChatUser chatUser = new ChatUser()
                .setUserId(ShiroUtils.getUserId())
                .setGender(myVo.getGender());
        this.chatUserService.updateById(chatUser);
        return AjaxResult.success("修改成功");
    }

    /**
     * 修改微聊号
     */
    @ApiVersion(VersionEnum.V1_0_0)
    @PostMapping("/editChatNo")
    public AjaxResult editChatNo(@Validated @RequestBody MyVo06 myVo) {
        // 执行修改
        this.chatUserService.editChatNo(myVo.getChatNo());
        return AjaxResult.success("修改成功");
    }

    /**
     * 修改个性签名
     */
    @ApiVersion(VersionEnum.V1_0_0)
    @PostMapping("/editIntro")
    public AjaxResult editIntro(@Validated @RequestBody MyVo07 myVo) {
        // 执行修改
        ChatUser chatUser = new ChatUser()
                .setUserId(ShiroUtils.getUserId())
                .setIntro(myVo.getIntro());
        this.chatUserService.updateById(chatUser);
        return AjaxResult.success("修改成功");
    }

    /**
     * 修改省市
     */
    @ApiVersion(VersionEnum.V1_0_0)
    @PostMapping("/editCity")
    public AjaxResult editCity(@Validated @RequestBody MyVo08 myVo) {
        // 执行修改
        ChatUser chatUser = new ChatUser()
                .setUserId(ShiroUtils.getUserId())
                .setProvinces(myVo.getProvinces())
                .setCity(myVo.getCity());
        this.chatUserService.updateById(chatUser);
        return AjaxResult.success("修改成功");
    }

    /**
     * 用户注销
     */
    @ApiVersion(VersionEnum.V1_0_0)
    @GetMapping("/deleted")
    public AjaxResult deleted() {
        this.chatUserService.deleted();
        return AjaxResult.success("注销成功");
    }

    /**
     * 建议反馈
     */
    @ApiVersion(VersionEnum.V1_0_0)
    @PostMapping("/feedback")
    public AjaxResult feedback(@Validated @RequestBody MyVo04 myVo) {
        this.feedbackService.addFeedback(myVo);
        return AjaxResult.success();
    }

    /**
     * 刷新
     */
    @ApiVersion(VersionEnum.V1_0_0)
    @GetMapping("/refresh")
    public AjaxResult refresh() {
        this.chatUserService.refresh();
        return AjaxResult.success();
    }


}
