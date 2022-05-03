package com.simple.game.core.domain.dto;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
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
	private static Logger logger = LoggerFactory.getLogger(OnlineInfo.class);

	/** 最后访问时间戳 ***/
	private long lastReqeustTime;

	/** 最后访问ip ***/
	private String lastIp;

	/** 最后登录ip ***/
	private long loginTime;

	/** 最后登录ip ***/
	private String loginIp;

	/** session ***/
	private GameSession session;

	/** 掉线时间戳 ***/
	private long disconnectTime;

	/** 是否在线 ***/
	public boolean isOnline() {
		return session != null;
	}

	/** 推送消息 ***/
	public void push(PushCmd pushCmd) {
		String message = JSON.toJSONString(pushCmd);
		try {
			byte[] data = message.getBytes("utf-8");
			session.write(data);
		} catch (UnsupportedEncodingException e) {
			logger.error("使用utf-8失敗", e);
		} catch (IOException e) {
			logger.error("寫入數據失敗", e);
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

	public Object getSession() {
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
