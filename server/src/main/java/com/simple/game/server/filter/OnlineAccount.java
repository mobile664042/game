package com.simple.game.server.filter;

import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.Session;

import com.simple.game.server.dbEntity.User;

import lombok.Getter;

@Getter
public class OnlineAccount{
	private final static ThreadLocal<OnlineAccount> LOGIN_CACHE = new ThreadLocal<OnlineAccount>();
	private User user;
	private String loginToken;
	
	/***
	 * 已连接的webSocket(游戏)
	 * key 		gameCode
	 * value 	WebSocketSession
	 */
	private final ConcurrentHashMap<String, Session> onlineWebSocket = new ConcurrentHashMap<String, Session>();
	
	private OnlineAccount() {};
	
	public static OnlineAccount valueOf(User user, String loginToken) {
		OnlineAccount onlineAccount = new OnlineAccount();
		onlineAccount.user = user;
		onlineAccount.loginToken = loginToken;
		return onlineAccount;
	}
	
	public static void cache(OnlineAccount onlineAccount) {
		LOGIN_CACHE.set(onlineAccount);
	}

	public static void clear() {
		LOGIN_CACHE.remove();
	}
	

	public static OnlineAccount get() {
		return LOGIN_CACHE.get();
	}
}
