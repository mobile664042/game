package com.simple.game.core.domain.cmd.req.game;

import com.simple.game.core.domain.cmd.push.game.PushConnectCmd;

import lombok.Data;

@Data
public class ReqConnectCmd extends ReqGameCmd{
	@Override
	public int getCode() {
		return 101008;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}

	public PushConnectCmd valueOfPushConnectCmd() {
		PushConnectCmd pushCmd = new PushConnectCmd();
		pushCmd.setDeskNo(deskNo);
		pushCmd.setPlayKind(playKind);
		pushCmd.setPlayerId(playerId);
		return pushCmd;
	}
}
