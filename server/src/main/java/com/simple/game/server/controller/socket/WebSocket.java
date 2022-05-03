package com.simple.game.server.controller.socket;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@ServerEndpoint("/websocket/{gameCode}/{loginToken}")
//此注解相当于设置访问URL
public class WebSocket {
	@Autowired
	private WebSocketHandler webSocketHandler;
	private Session session;
	
    @OnOpen
    public void onOpen(Session session,@PathParam(value="gameCode")String gameCode, @PathParam(value="loginToken")String loginToken) {
    	log.info("收到连接请求，gameCode={}, loginToken={}", gameCode, loginToken);
    	this.session = session;
    	webSocketHandler.onOpen(session, gameCode, loginToken);
    }

    @OnClose
    public void onClose() {
        webSocketHandler.onClose(session);
    }

    @OnMessage
    public void onMessage(String message) {
    	log.info("收到请求: {}", message);
    	webSocketHandler.onMessage(session, message);
    }

}