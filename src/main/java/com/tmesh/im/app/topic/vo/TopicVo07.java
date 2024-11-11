package com.tmesh.im.app.topic.vo;

import com.tmesh.im.app.topic.enums.TopicReplyTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
@Accessors(chain = true) // 链式调用
public class TopicVo07 {

    /**
     * 回复id
     */
    @NotNull(message = "回复id不能为空")
    private Long replyId;

    /**
     * 回复类型
     */
    @NotNull(message = "回复类型不能为空")
    private TopicReplyTypeEnum replyType;

    /**
     * 内容
     */
    @NotBlank(message = "内容不能为空")
    @Size(max = 2000, message = "内容长度不能大于2000")
    private String content;

}
