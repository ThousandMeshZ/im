package com.tmesh.im.app.chat.vo;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
public class FriendVo05 {

    @NotNull(message = "用户id不能为空")
    private Long userId;

    @NotBlank(message = "备注不能为空")
    @Size(max = 32, message = "备注长度不能大于32")
    private String remark;

}
