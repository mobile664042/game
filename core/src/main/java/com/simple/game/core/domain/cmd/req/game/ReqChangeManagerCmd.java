package com.simple.game.core.domain.cmd.req.game;

import com.simple.game.core.domain.cmd.push.game.PushChangeManagerCmd;

import lombok.Data;

@Data
public class ReqChangeManagerCmd extends ReqGameCmd{
	public final static int CMD = 101012;
	
	private Long otherId;
	@Override
	public int getCmd() {
		return CMD;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}

	public PushChangeManagerCmd valueOfPushChangeManagerCmd() {
		PushChangeManagerCmd pushCmd = new PushChangeManagerCmd();
//		pushCmd.setDeskNo(deskNo);
//		pushCmd.setPlayKind(playKind);
		if(otherId != null) {
			pushCmd.setPlayerId(otherId);
		}
		
		return pushCmd;
	}
}
