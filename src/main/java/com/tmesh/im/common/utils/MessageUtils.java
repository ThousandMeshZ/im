package com.tmesh.im.common.utils;

import cn.hutool.extra.spring.SpringUtil;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 获取 i18n 资源文件
 */

public class MessageUtils {
    /**
     * 根据消息键和参数 获取消息 委托给spring messageSource
     * <p>
     * msgCode 消息键
     * args    参数
     * 获取国际化翻译值
     */
    public static String message(String msgCode, Object... args) {
        MessageSource messageSource = SpringUtil.getBean(MessageSource.class);
        return messageSource.getMessage(msgCode, args, LocaleContextHolder.getLocale());
    }
}