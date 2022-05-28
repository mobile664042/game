package com.simple.game.core.domain.cmd.req.game;

import com.simple.game.core.domain.cmd.push.game.PushLeftCmd;

import lombok.Data;

@Data
public class ReqLeftCmd extends ReqGameCmd{
	public final static int CMD = 101005;
	@Override
	public int getCmd() {
		return CMD;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}

	public PushLeftCmd valueOfPushLeftCmd() {
		PushLeftCmd pushCmd = new PushLeftCmd();
//		pushCmd.setDeskNo(deskNo);
//		pushCmd.setPlayKind(playKind);
//		pushCmd.setPlayerId(playerId);
		return pushCmd;
	}
}
