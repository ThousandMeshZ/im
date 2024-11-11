package com.tmesh.im.common.aspectj;

import java.lang.annotation.*;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 忽略登录
 */

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented //可生成文档
public @interface IgnoreAuth {
}
