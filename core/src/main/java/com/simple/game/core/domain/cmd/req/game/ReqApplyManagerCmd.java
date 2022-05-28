package com.simple.game.core.domain.cmd.req.game;

import com.simple.game.core.domain.cmd.push.game.PushApplyManagerCmd;

import lombok.Data;

@Data
public class ReqApplyManagerCmd extends ReqGameCmd{
	public final static int CMD = 101011;
	
	@Override
	public int getCmd() {
		return CMD;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}

	public PushApplyManagerCmd valueOfPushApplyManagerCmd() {
		PushApplyManagerCmd pushCmd = new PushApplyManagerCmd();
//		pushCmd.setDeskNo(deskNo);
//		pushCmd.setPlayKind(playKind);
//		pushCmd.setPlayerId(playerId);
		return pushCmd;
	}
}
