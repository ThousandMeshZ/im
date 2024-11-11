package com.tmesh.im.app.ws;

import com.tmesh.im.app.push.service.ChatPushService;
import com.tmesh.im.common.constant.AppConstants;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : WebSocket 处理器
 */
@Slf4j
@Component
public class BootWebSocketHandler extends TextWebSocketHandler {

    private static final ConcurrentHashMap<Long, WebSocketSession> POOL_SESSION = new ConcurrentHashMap<>();

    @Lazy
    @Resource
    private ChatPushService chatPushService;

    /**
     * socket 建立成功事件
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        // 用户id
        Long userId = (Long) session.getAttributes().get(AppConstants.USER_ID);
        // 存储
        BootWebSocketHandler.POOL_SESSION.put(userId, session);
        // 离线消息
        this.chatPushService.pullMsg(userId);
    }

    /**
     * 接收消息事件
     */
    @SneakyThrows
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        // 获得客户端传来的消息
        String payload= message.getPayload();
        log.info("server 接收到消息 {}", payload);
        session.sendMessage(new TextMessage("ok"));
    }

    /**
     * socket 断开连接时
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Long userId = (Long) session.getAttributes().get(AppConstants.USER_ID);
        log.info("server 接收到消息 用户[{}] 断开连接，状态：{}", userId, status);
        this.closeSession(session);
    }

    /**
     * socket 异常连接时
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        Long userId = (Long) session.getAttributes().get(AppConstants.USER_ID);
        log.error("server 接收到消息 用户[{}] 连接异常，异常：{}", userId, exception.getMessage());
        this.closeSession(session);
        log.error(Arrays.toString(exception.getStackTrace()));
    }

    /**
     * 关闭session
     */
    private void closeSession(WebSocketSession session) {
        // 用户id
        Long userId = (Long) session.getAttributes().get(AppConstants.USER_ID);
        if (session.isOpen()) {
            try {
                session.close();
            } catch (IOException e) {
                log.error(e.getMessage());
                log.error(Arrays.toString(e.getStackTrace()));
            }
        }
        // 移除
        BootWebSocketHandler.POOL_SESSION.remove(userId);
    }

    /**
     * 给某个用户发送消息
     */
    public void sendMsg(Long userId, String content) {
        WebSocketSession session = BootWebSocketHandler.POOL_SESSION.get(userId);
        if (session == null) {
            return;
        }
        if (!session.isOpen()) {
            log.info("server 接收到消息 用户[{}] 连接已关闭", userId);
            this.closeSession(session);
            return;
        }
        try {
            session.sendMessage(new TextMessage(content));
        } catch (IOException e) {
            this.closeSession(session);
            log.error(e.getMessage());
            log.error(Arrays.toString(e.getStackTrace()));
        }
    }
}
