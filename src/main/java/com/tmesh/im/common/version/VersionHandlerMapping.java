package com.tmesh.im.common.version;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 版本号处理器
 */
public class VersionHandlerMapping extends RequestMappingHandlerMapping {

    @Override
    protected RequestCondition<VersionCondition> getCustomMethodCondition(Method method) {
        ApiVersion apiVersion = AnnotationUtils.findAnnotation(method, ApiVersion.class);
        return createCondition(apiVersion);
    }

    // 实例化 RequestCondition
    private RequestCondition<VersionCondition> createCondition(ApiVersion apiVersion) {
        if (apiVersion == null) {
            return null;
        }
        return new VersionCondition(apiVersion.value().getCode());
    }

}
