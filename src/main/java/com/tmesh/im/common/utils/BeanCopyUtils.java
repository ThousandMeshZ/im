package com.tmesh.im.common.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.util.List;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : java bean 复制操作的工具类
 */
public class BeanCopyUtils {

    /**
     * 包含方法
     */
    public static <T> T include(T source, String... fields) {
        JSONObject jsonObject = JSONUtil.parseObj(source);
        JSONObject target = new JSONObject();
        for (String field : fields) {
            target.set(field.trim(), jsonObject.get(field.trim()));
        }
        return target.toBean(new TypeReference<T>() {});
    }

    /**
     * 排除方法
     */
    public static <T> T exclude(T source, String... fields) {
        JSONObject target = JSONUtil.parseObj(source);
        for (String field : fields) {
            target.remove(field.trim());
        }
        return target.toBean(new TypeReference<T>() {});
    }
}
