package com.simple.game.core.domain.dto;

import com.simple.game.core.domain.cmd.push.PushCmd;

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
	/**最后访问时间戳***/
	private long lastReqeustTime;
	
	/**最后访问ip***/
	private String lastIp;
	
	/**最后登录ip***/
	private long loginTime;
	
	/**最后登录ip***/
	private String loginIp;
	
	/**session***/
	private Object session;
	
	/**掉线时间戳***/
	private long disconnectTime;
	
	/**是否在线***/
	public boolean isOnline() {
		return session != null;
	}
	
	
	
	/**推送消息***/
	public void push(PushCmd message) {}

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

	public void setSession(Object session) {
		this.session = session;
		disconnectTime = System.currentTimeMillis();
	}
	public long getDisconnectTime() {
		return disconnectTime;
	}
	
}
