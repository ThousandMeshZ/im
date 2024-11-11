package com.tmesh.im.common.aspectj;

import cn.hutool.extra.servlet.JakartaServletUtil;
import cn.hutool.json.JSONUtil;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.tmesh.im.common.shiro.ShiroUtils;
import com.tmesh.im.common.utils.ServletUtils;
import com.tmesh.im.common.web.domain.AjaxResult;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 提交切面
 */

@Aspect
@Configuration
public class SubmitAspect {

    private final Cache<String, Object> CACHES = CacheBuilder.newBuilder()
            // 最大缓存 100 个
            .maximumSize(100)
            // 设置缓存过期时间
            .expireAfterWrite(2, TimeUnit.SECONDS)
            .build();

    @SneakyThrows
    @Around("execution(* com.tmesh.im..*Controller.*(..)) && @annotation(submitRepeat)")
    public Object around(ProceedingJoinPoint joinPoint, SubmitRepeat submitRepeat) {
        String cacheKey = this.getCacheKey(submitRepeat);
        if (StringUtils.hasText(cacheKey)) {
            if (this.CACHES.getIfPresent(cacheKey) == null) {
                // 如果是第一次请求,就将key存入缓存中
                CACHES.put(cacheKey, cacheKey);
            } else {
                return AjaxResult.fail("请勿重复请求");
            }
        }
        return joinPoint.proceed();
    }

    /**
     * 加上用户的唯一标识
     */
    private String getCacheKey(SubmitRepeat submitRepeat) {
        StringBuilder builder = new StringBuilder();
        HttpServletRequest request = ServletUtils.getRequest();
        String param = null;
        // 如果登录获取登录参数
        if (ShiroUtils.isLogin()) {
            param = ShiroUtils.getToken();
        }
        // 获取param参数
        if (!StringUtils.hasText(param)) {
            Map<String, String> map = JakartaServletUtil.getParamMap(request);
            param = JSONUtil.toJsonStr(map);
        }
        builder.append(param);
        builder.append("_");
        // 获取 path 参数
        String path = submitRepeat.path();
        if (!StringUtils.hasText(path)) {
            path = request.getServletPath();
        }
        builder.append(path);
        return builder.toString();
    }


}
