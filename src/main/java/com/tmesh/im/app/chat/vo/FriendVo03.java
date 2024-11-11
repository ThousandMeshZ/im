package com.tmesh.im.app.chat.vo;

import com.tmesh.im.common.enums.YesOrNoEnum;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
public class FriendVo03 {

    @NotNull(message = "用户id不能为空")
    private Long userId;

    @NotNull(message = "状态不能为空")
    private YesOrNoEnum black;

}
