package com.simple.game.core.domain.cmd.req.game;

import com.simple.game.core.domain.cmd.push.game.PushChangeManagerCmd;

import lombok.Data;

@Data
public class ReqChangeManagerCmd extends ReqGameCmd{
	private Long otherId;
	@Override
	public int getCode() {
		return 101012;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}

	public PushChangeManagerCmd valueOfPushChatMultiCmd() {
		PushChangeManagerCmd pushCmd = new PushChangeManagerCmd();
		pushCmd.setDeskNo(deskNo);
		pushCmd.setPlayKind(playKind);
		pushCmd.setPlayerId(otherId);
		return pushCmd;
	}
}
