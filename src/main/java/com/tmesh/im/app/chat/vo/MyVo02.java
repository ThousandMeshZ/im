package com.tmesh.im.app.chat.vo;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class MyVo02 {

    @NotBlank(message = "头像不能为空")
    @Size(max = 2000, message = "头像长度不能大于2000")
    private String portrait;

}
