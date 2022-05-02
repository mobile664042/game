package com.simple.game.core.domain.cmd.req.game;

import com.simple.game.core.domain.cmd.push.game.PushKickoutCmd;

import lombok.Data;

@Data
public class ReqKickoutCmd extends ReqGameCmd{
	private Long otherId;
	@Override
	public int getCode() {
		return 101013;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}

	public PushKickoutCmd valueOfPushChatMultiCmd() {
		PushKickoutCmd pushCmd = new PushKickoutCmd();
		pushCmd.setDeskNo(deskNo);
		pushCmd.setPlayKind(playKind);
		pushCmd.setPlayerId(otherId);
		return pushCmd;
	}
}
