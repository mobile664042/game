package com.simple.game.server.filter;

import com.simple.game.server.dbEntity.User;

import lombok.Getter;

@Getter
public class OnlineAccount{
	private final static ThreadLocal<OnlineAccount> LOGIN_CACHE = new ThreadLocal<OnlineAccount>();
	private User user;
	private OnlineAccount() {};
	
	public static OnlineAccount valueOf(User user) {
		OnlineAccount onlineAccount = new OnlineAccount();
		onlineAccount.user = user;
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
