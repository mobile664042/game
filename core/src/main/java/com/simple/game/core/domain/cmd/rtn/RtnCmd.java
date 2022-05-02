package com.simple.game.core.domain.cmd.rtn;

import com.simple.game.core.domain.cmd.Cmd;

public abstract class RtnCmd extends Cmd{
	protected String message;
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
