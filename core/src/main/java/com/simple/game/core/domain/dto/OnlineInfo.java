package com.simple.game.core.domain.dto;

import java.io.IOException;

import com.simple.game.core.domain.cmd.push.PushCmd;
import com.simple.game.core.util.GameSession;

import lombok.ToString;

/***
 * 在线信息
 * 
 * @author zhibozhang
 *
 */
//@Data
@ToString
public class OnlineInfo {

	/** 最后访问时间戳 ***/
	private long lastReqeustTime;

	/** 最后访问ip ***/
	private String lastIp;

	/** 最后登录ip ***/
	private long loginTime;

	/** 最后登录ip ***/
	private String loginIp;

	/** session
	 * 可以缓存游戏在线信息（com.simple.game.core.domain.dto.GameSessionInfo）
	 ****/
	private GameSession session;

	/** 掉线时间戳 ***/
	private long disconnectTime;

	/** 是否在线 ***/
	public boolean isOnline() {
		return session != null;
	}

	/** 推送消息 ***/
	public void push(PushCmd pushCmd) {
		if(session != null && session.isOpen()) {
			session.write(pushCmd);
		}
	}

	public long getLastReqeustTime() {
		return lastReqeustTime;
	}

	public void setLastReqeustTime(long lastReqeustTime) {
		this.lastReqeustTime = lastReqeustTime;
	}

	public String getLastIp() {
		return lastIp;
	}

	public void setLastIp(String lastIp) {
		this.lastIp = lastIp;
	}

	public long getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(long loginTime) {
		this.loginTime = loginTime;
	}

	public String getLoginIp() {
		return loginIp;
	}

	public void setLoginIp(String loginIp) {
		this.loginIp = loginIp;
	}
	
	public void closeSession() throws IOException {
		if(session != null) {
			session.close();
		}
	}

	public GameSession getSession() {
		return session;
	}

	public void setSession(GameSession session) {
		this.session = session;
		disconnectTime = System.currentTimeMillis();
	}

	public long getDisconnectTime() {
		return disconnectTime;
	}

}
