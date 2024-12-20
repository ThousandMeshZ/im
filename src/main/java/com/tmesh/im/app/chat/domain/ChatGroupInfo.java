package com.tmesh.im.app.chat.domain;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tmesh.im.common.enums.YesOrNoEnum;
import com.tmesh.im.common.web.domain.BaseEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 群组信息 实体类
 */
@Data
@TableName("chat_group_info")
@Accessors(chain = true) // 链式调用
@NoArgsConstructor
public class ChatGroupInfo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId
    private Long infoId;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 群组id
     */
    private Long groupId;
    /**
     * 是否置顶
     */
    private YesOrNoEnum top;
    /**
     * 是否免打扰
     */
    private YesOrNoEnum disturb;
    /**
     * 是否保存群组
     */
    private YesOrNoEnum keepGroup;
    /**
     * 加入时间
     */
    private Date createTime;

    public ChatGroupInfo(Long userId, Long groupId) {
        this.userId = userId;
        this.groupId = groupId;
        this.createTime = DateUtil.date();
        this.top = YesOrNoEnum.NO;
        this.disturb = YesOrNoEnum.NO;
        this.keepGroup = YesOrNoEnum.NO;
    }
}
