package com.simple.game.core.domain.cmd.req;

import com.simple.game.core.domain.cmd.Cmd;

public abstract class ReqCmd extends Cmd{
	protected long playerId;

	public long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}
	
	public void checkParam() {};
}
