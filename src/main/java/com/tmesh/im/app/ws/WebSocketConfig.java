package com.tmesh.im.app.ws;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : WebSocket 配置类
 */
/*
1、如果使用默认的嵌入式容器 比如 Tomcat 则必须手工在上下文提供 ServerEndpointExporter。
2、如果使用外部容器部署 war 包，则不需要提供提供 ServerEndpointExporter，
    因为此时 SpringBoot 默认将扫描 服务端的行为交给外部容器处理，
    所以线上部署的时候要把 WebSocketConfig 中这段注入 bean 的代码注掉
*/
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private BootWebSocketHandler bootWebSocketHandler;

    @Autowired
    private BootWebSocketInterceptor bootWebSocketInterceptor;

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry
                .addHandler(this.bootWebSocketHandler, "/ws")
                .addInterceptors(this.bootWebSocketInterceptor)
                .setAllowedOrigins("*");
    }
}
