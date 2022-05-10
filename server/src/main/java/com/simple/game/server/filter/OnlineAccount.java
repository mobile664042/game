package com.simple.game.server.filter;

import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;
import javax.websocket.Session;

import com.simple.game.server.dbEntity.User;

import lombok.Data;
import lombok.Getter;

@Getter
public class OnlineAccount{
	private final static ThreadLocal<OnlineAccount> LOGIN_CACHE = new ThreadLocal<OnlineAccount>();
	private User user;
	private String loginToken;
	private HttpSession session;
	
	/***
	 * 已连接的webSocket(游戏)
	 * key 		gameCode
	 * value 	GameOnlineInfo
	 */
	private final ConcurrentHashMap<String, GameOnlineInfo> onlineWebSocket = new ConcurrentHashMap<String, GameOnlineInfo>();
	
	
	
	
	
	private OnlineAccount() {};
	
	public static OnlineAccount valueOf(User user, String loginToken, HttpSession session) {
		OnlineAccount onlineAccount = new OnlineAccount();
		onlineAccount.user = user;
		onlineAccount.loginToken = loginToken;
		onlineAccount.session = session;
		return onlineAccount;
	}
	
	public void changeSession(HttpSession session) {
		this.session = session;
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
	
	@Data
	public static class GameOnlineInfo{
		Session session;
	    int playKind;
		int deskNo;
	}
}
