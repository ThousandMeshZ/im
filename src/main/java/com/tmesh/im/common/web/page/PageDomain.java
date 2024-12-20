package com.tmesh.im.common.web.page;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 分页数据
 */

@Data
@NoArgsConstructor
public class PageDomain {

    /**
     * 当前记录起始索引
     */
    private Integer pageNum;
    /**
     * 每页显示记录数
     */
    private Integer pageSize;
    /**
     * 排序列
     */
    private String orderBy;
    /**
     * 排序方向 "desc" 或者 "asc"
     */
    private String orderSort;
    /**
     * 记录总数
     */
    private Long total;

    public String getOrderBy() {
        if (!StringUtils.hasText(orderBy)) {
            return "";
        }
        return StrUtil.toUnderlineCase(orderBy) + "" + orderSort;
    }

    public Integer getPageStart() {
        return (getPageNum() - 1) * getPageSize();
    }

    public Integer getPageEnd() {
        return getPageStart() + getPageSize();
    }

    /**
     * 检查字符，防止注入绕过
     */
    public static String escapeOrderBySql(String value) {
        // 仅支持字母、数字、下划线、空格、逗号（支持多个字段排序）
        String rePattern = "[a-zA-Z0-9_\\ \\,]+";
        if (StringUtils.hasText(value) && !value.matches(rePattern)) {
            return "";
        }
        return StrUtil.toUnderlineCase(value);
    }
}
