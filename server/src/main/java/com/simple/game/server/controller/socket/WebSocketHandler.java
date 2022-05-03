package com.simple.game.server.controller.socket;

import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.simple.game.server.filter.OnlineAccount;
import com.simple.game.server.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class WebSocketHandler {
	/***
	 * 已连接的webSocket(游戏)
	 * key 		gameCode--webSocketId
	 * value 	WebSocketSession
	 */
	private final ConcurrentHashMap<String, OnlineAccount> gameOnlineAccountMap = new ConcurrentHashMap<String, OnlineAccount>();
	
	private final String supportGameCode = "ddz";
	@Autowired
    private UserService userService;
	
	@Autowired
	private WebGameDispatcher webGameDispatcher;

    public void onOpen(Session session, String gameCode, String loginToken) {
    	if(!supportGameCode.equals(gameCode)) {
    		log.warn("不好意思，还不支持{}游戏, loginToken={}", gameCode, loginToken);
    		return ;
    	}
    	
    	OnlineAccount onlineAccount = userService.getOnlineAccount(loginToken);
    	if(onlineAccount == null) {
    		log.warn("不好意思，loginToken={}已失效", loginToken);
    		return; 
    	}
    	onlineAccount.getOnlineWebSocket().put(gameCode, session);
    	String onlineKey = buildOnlineKey(gameCode, session);
    	gameOnlineAccountMap.put(onlineKey, onlineAccount);
    	
    	log.info("好棒哦，{}登录了游戏，gameCode={}, loginToken={}", onlineAccount.getUser().getUsername(), gameCode, loginToken);
    }

	public void onClose(Session session) {
		String gameCode = session.getRequestParameterMap().get("gameCode").get(0);
		String onlineKey = buildOnlineKey(gameCode, session);
		
		OnlineAccount onlineAccount = gameOnlineAccountMap.remove(onlineKey);
    	if(onlineAccount == null) {
    		log.warn("不好意思，onlineKey={}已提前失效了", onlineKey);
    		return; 
    	}
    	onlineAccount.getOnlineWebSocket().remove(gameCode);
    	log.info("{}用户离开了{}游戏,loginToken={}", onlineAccount.getUser().getUsername(), gameCode, onlineAccount.getLoginToken());
	}

	public void onMessage(Session session, String message) {
		String gameCode = session.getRequestParameterMap().get("gameCode").get(0);
		String onlineKey = buildOnlineKey(gameCode, session);
		
		OnlineAccount onlineAccount = gameOnlineAccountMap.get(onlineKey);
		if(onlineAccount == null) {
			log.warn("不好意思，onlineKey={}已提前失效了", onlineKey);
    		return; 
		}
		webGameDispatcher.onMessage(gameCode, message, onlineAccount);
	}

	private String buildOnlineKey(String gameCode, Session session) {
		return gameCode + "--" + session.getId();
	}
}