package com.simple.game.core.domain.cmd.push;

import com.simple.game.core.domain.cmd.Cmd;

public abstract class PushCmd extends Cmd{
	private String message;
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
