package com.tmesh.im.app.chat.vo;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
public class GroupVo03 {

    @NotNull(message = "群id不能为空")
    private Long groupId;

    @NotBlank(message = "群组公告不能为空")
    @Size(max = 200, message = "群组公告长度不能大于200")
    private String notice;
}
