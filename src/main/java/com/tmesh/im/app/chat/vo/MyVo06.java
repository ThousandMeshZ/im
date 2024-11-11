package com.tmesh.im.app.chat.vo;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class MyVo06 {

    @NotBlank(message = "微聊号不能为空")
    @Size(min = 6, max = 20, message = "微聊号长度限6-20位")
    private String chatNo;

}
