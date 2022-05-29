package com.simple.game.core.robot;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.alibaba.fastjson.JSON;
import com.simple.game.core.domain.cmd.Cmd;
import com.simple.game.core.util.GameSession;

import lombok.extern.slf4j.Slf4j;

/***
 * 内置机器人session
 * @author Administrator
 *
 */
@Slf4j
public class RobotGameSession implements GameSession {
	/****桌号位序***/
	protected final static AtomicInteger NUMBER_INDEX = new AtomicInteger(1001); 
	
	private final Map<String, Object> attachment = new HashMap<String, Object>();
	
	private final int sessionId;
	private final ActionListener actionListener;
	
	public RobotGameSession(ActionListener actionListener) {
		this.sessionId = NUMBER_INDEX.getAndIncrement();
		this.actionListener = actionListener;
	}

	@Override
	public boolean isOpen() {
		return true;
	}

	@Override
	public long getMaxIdleTimeout() {
		return 10000000000L;
	}

	@Override
	public String getId() {
		return String.valueOf(sessionId);
	}

	@Override
	public String getRemoteAddr() {
		return null;
	}

	@Override
	public void close() throws IOException {
	}

	@Override
	public void write(byte[] data) throws IOException{
	}
	
	@Override
	public void write(String text){
		log.debug("收到：{}", text);
	}
	
	@Override
	public void write(Cmd cmd){
		actionListener.submitEvent(this, cmd);
	}

	@Override
	public Map<String, Object> getAttachment() {
		return attachment;
	}

}
