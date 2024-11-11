package com.tmesh.im.app.topic.vo;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class TopicVo02 {

    @NotBlank(message = "封面不能为空")
    @Size(max = 2000, message = "封面长度不能大于2000")
    private String cover;

}
