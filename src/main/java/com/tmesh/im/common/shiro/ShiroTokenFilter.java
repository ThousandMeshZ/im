package com.tmesh.im.common.shiro;


import cn.hutool.json.JSONUtil;
import com.tmesh.im.common.aspectj.IgnoreAuth;
import com.tmesh.im.common.constant.HeadConstant;
import com.tmesh.im.common.enums.ResultCodeEnum;
import com.tmesh.im.common.exception.BaseException;
import com.tmesh.im.common.exception.LoginException;
import com.tmesh.im.common.web.domain.AjaxResult;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.io.IOException;
import java.lang.annotation.Annotation;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : auth2 过滤器
 */
public class ShiroTokenFilter extends AuthenticatingFilter {
    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) {
        //获取请求 token
        ShiroLoginToken token = this.getToken(request);
        if (token == null) {
            return null;
        }
        return token;
    }

    @SneakyThrows
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        if (RequestMethod.OPTIONS.name().equalsIgnoreCase(httpServletRequest.getMethod())) {
            return true;
        }
        if ("/favicon.ico".equals(httpServletRequest.getRequestURI())) {
            return error(request, response, ResultCodeEnum.SUCCESS);
        }
        try {
            WebApplicationContext webApplicationContext = RequestContextUtils.findWebApplicationContext(httpServletRequest);
            RequestMappingHandlerMapping requestMapping = webApplicationContext.getBean(RequestMappingHandlerMapping.class);
            HandlerExecutionChain handler = requestMapping.getHandler(httpServletRequest);
            if (handler == null) {
                return true;
            }
            Annotation[] declaredAnnotations = ((HandlerMethod) handler.getHandler()).getMethod().getDeclaredAnnotations();
            if (declaredAnnotations.length != 0) {
                for (Annotation annotation : declaredAnnotations) {
                    if (IgnoreAuth.class.equals(annotation.annotationType())) {
                        return true;
                    }
                }
            }
        } catch (BaseException e) {
            e.printStackTrace();
            return error(request, response, e.getResultCode());
        } catch (Exception e) {
            e.printStackTrace();
            return error(request, response, ResultCodeEnum.FAIL);
        }
        return false;
    }
    
    private boolean error(ServletRequest request, ServletResponse response, ResultCodeEnum resultCode) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        try {
            // 请求转发 401 controller
            httpServletRequest.getRequestDispatcher("/error/" + resultCode.getCode()).forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        // 请求token
        ShiroLoginToken token = getToken(request);
        if (token == null) {
            return error(response, ResultCodeEnum.UNAUTHORIZED, null);
        }
        try {
            this.getSubject(request, response).login(token);
            return true;
        } catch (LoginException e) {
            e.printStackTrace();
            return error(response, ResultCodeEnum.UNAUTHORIZED, e.getMessage());
        } catch (AuthenticationException e) {
            e.printStackTrace();
            return error(response, ResultCodeEnum.UNAUTHORIZED, null);
        }
    }

    private boolean error(ServletResponse response, ResultCodeEnum resultCode, String msg) throws IOException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setContentType("application/json;charset=utf-8");
        httpResponse.getWriter().print(JSONUtil.toJsonStr(AjaxResult.result(resultCode, msg)));
        return false;
    }

    /**
     * 请求的 token
     */
    private ShiroLoginToken getToken(ServletRequest request) {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String token = httpRequest.getHeader(HeadConstant.TOKEN_HEADER_ADMIN);
        if (StringUtils.isEmpty(token)) {
            token = httpRequest.getParameter(HeadConstant.TOKEN_HEADER_ADMIN);
        }
        if (!StringUtils.isEmpty(token)) {
            return new ShiroLoginToken(token);
        }
        return null;
    }
}
