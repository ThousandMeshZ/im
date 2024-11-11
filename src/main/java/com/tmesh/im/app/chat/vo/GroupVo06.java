package com.tmesh.im.app.chat.vo;

import com.tmesh.im.common.enums.YesOrNoEnum;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
public class GroupVo06 {

    @NotNull(message = "群id不能为空")
    private Long groupId;

    @NotNull(message = "状态不能为空")
    private YesOrNoEnum keepGroup;
}
