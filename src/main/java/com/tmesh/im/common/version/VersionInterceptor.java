package com.tmesh.im.common.version;

import com.tmesh.im.common.constant.HeadConstant;
import com.tmesh.im.common.enums.ResultCodeEnum;
import com.tmesh.im.common.enums.YesOrNoEnum;
import com.tmesh.im.common.exception.BaseException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 版本验证
 */
@Component
@Slf4j
public class VersionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        /**
         * 如果不属于HandlerMethod，则放行
         */
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        if (YesOrNoEnum.NO.equals(VersionConfig.ENABLED)) {
            return true;
        }
        String currentUrl = request.getServletPath();
        if (VersionUtils.verifyUrl(currentUrl, VersionConfig.EXCLUDES)) {
            return true;
        }
        //获取用户接口版本
        String version = request.getHeader(HeadConstant.VERSION);
        if (VersionUtils.compareTo(version, VersionConfig.VERSION, request.getRequestURI()) < 0) {
            throw new BaseException(ResultCodeEnum.VERSION);
        }
        return true;

    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response, Object object, Exception e) {
    }

}
