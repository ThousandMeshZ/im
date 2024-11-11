package com.tmesh.im.app.chat.vo;

import com.tmesh.im.app.chat.enums.ApplySourceEnum;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
public class FriendVo02 {

    @NotNull(message = "用户id不能为空")
    private Long userId;

    @NotBlank(message = "申请理由不能为空")
    @Size(max = 20, message = "申请理由长度不能大于20")
    private String reason;

    @NotNull(message = "好友来源不能为空")
    private ApplySourceEnum source;

}
