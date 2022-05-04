package com.simple.game.core.domain.cmd.push;

import com.simple.game.core.domain.cmd.Cmd;

public abstract class PushCmd extends Cmd{
	public final static int NOTIFY_NUM = 2000000;
	public final static int PUSH_NUM = 1000000;
	
	private String message;
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
