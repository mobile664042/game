package com.simple.game.core.domain.cmd.req.game;

import com.simple.game.core.domain.cmd.push.game.PushDisconnectCmd;

import lombok.Data;

@Data
public class ReqDisconnectCmd extends ReqGameCmd{
	public final static int CMD = 101007;
	
	@Override
	public int getCmd() {
		return CMD;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}

	public PushDisconnectCmd valueOfPushDisconnectCmd() {
		PushDisconnectCmd pushCmd = new PushDisconnectCmd();
		pushCmd.setDeskNo(deskNo);
		pushCmd.setPlayKind(playKind);
		pushCmd.setPlayerId(playerId);
		return pushCmd;
	}
}
