package com.tmesh.im.common.core;

import cn.hutool.core.util.EnumUtil;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 类型转换器
 */
public class EnumUtils {
    
    public static <E extends Enum<E>> E toEnum(Class<E> clazz, String code) {
        return toEnum(clazz, code, null);
    }
    
    public static <E extends Enum<E>> E toEnum(Class<E> clazz, String code, E defaultValue) {
        /* 
        * StringUtils.isEmpty() as of 5.3, in favor of hasLength(String) and hasText(String) (or ObjectUtils.isEmpty(Object))
        */
//        if (StringUtils.isEmpty()) {
        if (!StringUtils.hasText(code)) {
            return defaultValue;
        }
        Map<String, Object> enumMap = EnumUtil.getNameFieldMap(clazz, "code");
        for (String key : enumMap.keySet()) {
            if (code.equals(enumMap.get(key).toString())) {
                return EnumUtil.fromString(clazz, key);
            }
        }
        return defaultValue;
    }
}
