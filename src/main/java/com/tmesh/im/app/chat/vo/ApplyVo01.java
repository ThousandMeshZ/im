package com.tmesh.im.app.chat.vo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class ApplyVo01 {

    @NotNull(message = "申请id不能为空")
    private Long applyId;

}
