package com.tmesh.im.app.chat.vo;

import com.tmesh.im.common.enums.GenderEnum;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
public class MyVo05 {

    @NotNull(message = "性别不能为空")
    private GenderEnum gender;

}
