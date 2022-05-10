package com.simple.game.server.controller.socket;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.springframework.stereotype.Component;

import com.simple.game.server.constant.MyConstant;
import com.simple.game.server.util.SpringContextUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@ServerEndpoint("/websocket/{gameCode}/{loginToken}")
//此注解相当于设置访问URL
public class WebSocket {
	private Session session;
	
    @OnOpen
    public void onOpen(Session session,@PathParam(value=MyConstant.GAME_CODE)String gameCode, @PathParam(value=MyConstant.LOGIN_TOKEN)String loginToken) {
    	log.info("收到连接请求，gameCode={}, loginToken={}", gameCode, loginToken);
    	this.session = session;
    	WebSocketHandler webSocketHandler = getWebSocketHandler();
    	webSocketHandler.onOpen(session, gameCode, loginToken);
    }

    @OnClose
    public void onClose() {
    	WebSocketHandler webSocketHandler = getWebSocketHandler();
        webSocketHandler.onClose(session);
    }

    @OnMessage
    public void onMessage(String message) {
    	log.info("收到请求: {}, sessionId={}", message, session.getId());
    	WebSocketHandler webSocketHandler = getWebSocketHandler();
    	webSocketHandler.onMessage(session, message);
    }
    
    private WebSocketHandler getWebSocketHandler() {
    	return SpringContextUtil.getApplicationContext().getBean(WebSocketHandler.class);
    }

}