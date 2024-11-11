package com.tmesh.im.app.chat.controller;

import com.tmesh.im.app.chat.service.ChatMsgService;
import com.tmesh.im.app.chat.vo.ChatVo01;
import com.tmesh.im.common.version.ApiVersion;
import com.tmesh.im.common.version.VersionEnum;
import com.tmesh.im.common.web.controller.BaseController;
import com.tmesh.im.common.web.domain.AjaxResult;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 聊天控制器
 */
@RestController
@Slf4j
@RequestMapping("/chat")
public class ChatController extends BaseController {

    @Resource
    private ChatMsgService chatMsgService;

    /**
     * 发送信息
     */
    @ApiVersion(VersionEnum.V1_0_0)
    @PostMapping("/sendMsg")
    public AjaxResult sendMsg(@Validated @RequestBody ChatVo01 chatVo) {
        return AjaxResult.success(this.chatMsgService.sendFriendMsg(chatVo));
    }

}
