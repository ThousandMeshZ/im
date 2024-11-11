package com.tmesh.im.app.chat.vo;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
public class FriendVo01 {

    @NotBlank(message = "搜索参数不能为空")
    private String param;

}
