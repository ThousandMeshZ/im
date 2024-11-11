package com.tmesh.im.common.web.aspectj;

import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : Mybatis的切面
 */

@Aspect
@Component
public class MybatisAspectj {

    /**
     * 配置查询一个织入点
     */
    @Pointcut("execution(public * com.baomidou.mybatisplus.core.mapper.BaseMapper.selectOne(..))")
    public void selectOneAspect() {
    }

    /**
     * 配置查询一个前置通知
     */
    @Before("selectOneAspect()")
    public void beforeSelect(JoinPoint point) {
        Object arg = point.getArgs()[0];
        if (arg instanceof AbstractWrapper<?,?,?>) {
            ((AbstractWrapper) arg).last("limit 1");
        }
    }
}