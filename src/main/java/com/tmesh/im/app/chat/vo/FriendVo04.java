package com.tmesh.im.app.chat.vo;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
public class FriendVo04 {

    @NotNull(message = "用户id不能为空")
    private Long userId;

}
