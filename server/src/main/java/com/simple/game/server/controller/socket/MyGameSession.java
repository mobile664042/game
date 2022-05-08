package com.simple.game.server.controller.socket;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.websocket.Session;

import com.alibaba.fastjson.JSON;
import com.simple.game.core.domain.cmd.Cmd;
import com.simple.game.core.util.GameSession;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyGameSession implements GameSession {
	private Session session;
	
	public MyGameSession(Session session) {
		this.session = session;
	}

	@Override
	public boolean isOpen() {
		return session.isOpen();
	}

	@Override
	public long getMaxIdleTimeout() {
		return session.getMaxIdleTimeout();
	}

	@Override
	public String getId() {
		return session.getId();
	}

	@Override
	public String getRemoteAddr() {
		return null;
	}

	@Override
	public void close() throws IOException {
		session.close();
	}

	@Override
	public void write(byte[] data) throws IOException{
		ByteBuffer buff = ByteBuffer.wrap(data);
		session.getBasicRemote().sendBinary(buff);
	}
	
	@Override
	public void write(String text){
		try {
			session.getBasicRemote().sendText(text);
		} catch (Exception e) {
			log.error("写入{}失败", text, e);
		}
	}
	
	@Override
	public void write(Cmd cmd){
		String json = JSON.toJSONString(cmd);
		write(json);
	}

}
