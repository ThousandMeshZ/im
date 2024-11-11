package com.tmesh.im.app.chat.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true) // 链式调用
public class GroupVo08 {

    /**
     * 群id
     */
    private Long groupId;
    /**
     * 群名
     */
    private String name;
    /**
     * 头像
     */
    private List<String> portrait;

}
