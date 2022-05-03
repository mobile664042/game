package com.simple.game.server.controller.socket;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.websocket.Session;

import com.simple.game.core.util.GameSession;

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

}
