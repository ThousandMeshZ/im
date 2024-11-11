package com.tmesh.im.app.chat.vo;

import com.tmesh.im.app.push.enums.PushMsgEnum;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
public class ChatVo01 {

    @NotNull(message = "用户不能为空")
    private Long userId;

    @NotNull(message = "消息类型不能为空")
    private PushMsgEnum msgType;

    @NotBlank(message = "消息内容不能为空")
    @Size(max = 20000, message = "消息内容长度不能大于20000")
    private String content;

}
