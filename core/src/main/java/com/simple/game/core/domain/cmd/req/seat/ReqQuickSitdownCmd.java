package com.simple.game.core.domain.cmd.req.seat;

import com.simple.game.core.domain.cmd.push.seat.PushSitdownCmd;
import com.simple.game.core.domain.cmd.req.game.ReqGameCmd;

import lombok.Data;

@Data
public class ReqQuickSitdownCmd extends ReqGameCmd{
	public final static int CMD = 102004;
	
	@Override
	public int getCmd() {
		return CMD;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}

	public PushSitdownCmd valueOfPushSitdownCmd() {
		PushSitdownCmd pushCmd = new PushSitdownCmd();
		pushCmd.setDeskNo(deskNo);
		pushCmd.setPlayKind(playKind);
		return pushCmd;
	}
}
