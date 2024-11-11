package com.tmesh.im.common.utils;

import cn.hutool.core.convert.Convert;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : Servlet 工具类
 */
public class ServletUtils {

    /**
     * 获取String参数
     */
    public static String getParameter(String name) {
        return ServletUtils.getRequest().getParameter(name);
    }

    /**
     * 获取String参数
     */
    public static String getParameter(String name, String defaultValue) {
        String value = ServletUtils.getParameter(name);
        if (!StringUtils.hasText(value)) {
            value = defaultValue;
        }
        return value;
    }

    /**
     * 获取Integer参数
     */
    public static Integer getParameterToInt(String name) {
        return ServletUtils.getParameterToInt(name, null);
    }

    /**
     * 获取Integer参数
     */
    public static Integer getParameterToInt(String name, Integer defaultValue) {
        return Convert.toInt(ServletUtils.getRequest().getParameter(name), defaultValue);
    }

    /**
     * 获取request
     */
    public static HttpServletRequest getRequest() {
        return ServletUtils.getRequestAttributes().getRequest();
    }

    /**
     * 获取response
     */
    public static HttpServletResponse getResponse() {
        return ServletUtils.getRequestAttributes().getResponse();
    }

    /**
     * 获取sessionId
     */
    public static String getSessionId() {
        return ServletUtils.getRequestAttributes().getSessionId();
    }

    public static ServletRequestAttributes getRequestAttributes() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        return (ServletRequestAttributes) attributes;
    }
}
