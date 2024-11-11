package com.tmesh.im.app.ws;

import cn.hutool.extra.spring.SpringUtil;
import com.tmesh.im.app.auth.service.TokenService;
import com.tmesh.im.common.constant.AppConstants;
import com.tmesh.im.common.constant.HeadConstant;
import com.tmesh.im.common.shiro.LoginUser;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.Map;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : WebSocket 拦截器
 */
@Component
public class BootWebSocketInterceptor extends HttpSessionHandshakeInterceptor {

    @Lazy
    @Resource
    private TokenService tokenService;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {
        // 接受前端传来的参数
        String token = ((ServletServerHttpRequest) request).getServletRequest().getParameter(HeadConstant.TOKEN_HEADER_ADMIN);
        if (!StringUtils.hasText(token)) {
            return false;
        }
//        TokenService tokenService = SpringUtil.getBean("tokenService");
        LoginUser loginUser = tokenService.queryByToken(token);
        if (loginUser == null) {
            return false;
        }
        //将参数放到 attributes
        attributes.put(AppConstants.USER_ID, loginUser.getUserId());
        return super.beforeHandshake(request, response, wsHandler, attributes);
    }

}
