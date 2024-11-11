package com.tmesh.im.common.web.exception;

import com.tmesh.im.common.web.domain.AjaxResult;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 错误属性处理工具
 */


@Component
public class ErrorAttributesCustom extends DefaultErrorAttributes {

    /**
     * DefaultErrorAttributes 是 Spring Boot 的默认错误属性处理工具，它可以从请求中获取异常或错误信息，并将其封装为一个 Map 对象返回。
     */

    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
        super.getErrorAttributes(webRequest, options);
        return AjaxResult.fail();
    }

}

