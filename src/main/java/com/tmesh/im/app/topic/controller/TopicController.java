package com.tmesh.im.app.topic.controller;

import com.tmesh.im.app.chat.domain.ChatUser;
import com.tmesh.im.app.chat.service.ChatUserService;
import com.tmesh.im.app.topic.service.ChatTopicService;
import com.tmesh.im.app.topic.vo.TopicVo01;
import com.tmesh.im.app.topic.vo.TopicVo02;
import com.tmesh.im.app.topic.vo.TopicVo07;
import com.tmesh.im.common.shiro.ShiroUtils;
import com.tmesh.im.common.version.ApiVersion;
import com.tmesh.im.common.version.VersionEnum;
import com.tmesh.im.common.web.controller.BaseController;
import com.tmesh.im.common.web.domain.AjaxResult;
import com.tmesh.im.common.web.page.TableDataInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 帖子
 */
@RestController
@Slf4j
@RequestMapping("/topic")
public class TopicController extends BaseController {

    @Resource
    private ChatTopicService topicService;

    @Resource
    private ChatUserService chatUserService;

    /**
     * 修改封面
     */
    @ApiVersion(VersionEnum.V1_0_0)
    @PostMapping("/editCover")
    public AjaxResult editCover(@RequestBody @Validated TopicVo02 topicVo) {
        // 执行修改
        ChatUser chatUser = new ChatUser()
                .setUserId(ShiroUtils.getUserId())
                .setCover(topicVo.getCover());
        this.chatUserService.updateById(chatUser);
        return AjaxResult.success("修改成功");
    }

    /**
     * 发布帖子
     */
    @ApiVersion(VersionEnum.V1_0_0)
    @PostMapping("/sendTopic")
    public AjaxResult sendTopic(@RequestBody @Validated TopicVo01 topicVo) {
        this.topicService.sendTopic(topicVo);
        return AjaxResult.success();
    }

    /**
     * 删除帖子
     */
    @ApiVersion(VersionEnum.V1_0_0)
    @GetMapping("/removeTopic/{topicId}")
    public AjaxResult removeTopic(@PathVariable Long topicId) {
        this.topicService.delTopic(topicId);
        return AjaxResult.success();
    }

    /**
     * 指定人的帖子
     */
    @ApiVersion(VersionEnum.V1_0_0)
    @GetMapping("/userTopic/{userId}")
    public TableDataInfo userTopic(@PathVariable Long userId) {
        return getDataTable(this.topicService.userTopic(userId));
    }

    /**
     * 好友的帖子
     */
    @ApiVersion(VersionEnum.V1_0_0)
    @GetMapping("/topicList")
    public TableDataInfo topicList() {
        startPage("create_time desc");
        return getDataTable(this.topicService.topicList());
    }

    /**
     * 帖子详情
     */
    @ApiVersion(VersionEnum.V1_0_0)
    @GetMapping("/topicInfo/{topicId}")
    public AjaxResult topicInfo(@PathVariable Long topicId) {
        return AjaxResult.success(this.topicService.topicInfo(topicId));
    }

    /**
     * 点赞
     */
    @ApiVersion(VersionEnum.V1_0_0)
    @GetMapping("/like/{topicId}")
    public AjaxResult like(@PathVariable Long topicId) {
        this.topicService.like(topicId);
        return AjaxResult.success();
    }

    /**
     * 取消点赞
     */
    @ApiVersion(VersionEnum.V1_0_0)
    @GetMapping("/cancelLike/{topicId}")
    public AjaxResult cancelLike(@PathVariable Long topicId) {
        this.topicService.cancelLike(topicId);
        return AjaxResult.success();
    }

    /**
     * 回复
     */
    @ApiVersion(VersionEnum.V1_0_0)
    @PostMapping("/reply")
    public AjaxResult reply(@Validated @RequestBody TopicVo07 topicVo) {
        return AjaxResult.success(this.topicService.reply(topicVo));
    }

    /**
     * 删除回复
     */
    @ApiVersion(VersionEnum.V1_0_0)
    @GetMapping("/removeReply/{replyId}")
    public AjaxResult removeReply(@PathVariable Long replyId) {
        this.topicService.delReply(replyId);
        return AjaxResult.success();
    }

    /**
     * 查询通知列表
     */
    @ApiVersion(VersionEnum.V1_0_0)
    @GetMapping("/noticeList")
    public AjaxResult noticeList() {
        return AjaxResult.success(this.topicService.queryNoticeList());
    }

    /**
     * 清空通知列表
     */
    @ApiVersion(VersionEnum.V1_0_0)
    @GetMapping("/clearNotice")
    public AjaxResult clearNotice() {
        this.topicService.clearNotice();
        return AjaxResult.success();
    }
}
