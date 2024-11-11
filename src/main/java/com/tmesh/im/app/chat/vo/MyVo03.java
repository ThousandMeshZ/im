package com.tmesh.im.app.chat.vo;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class MyVo03 {

    @NotBlank(message = "昵称不能为空")
    @Size(max = 20, message = "昵称长度不能大于20")
    private String nickName;

}
